package dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.DBManager;
import dto.Sale;

public class SaleDao {
	
	public int insertSale(Sale sale) {
        int ret = -1;
        String sql = "insert into sales (custid, phoneid, quantity, total_price) values ( ?, ?, ?, ? ); ";
        
        Connection con = null;
        PreparedStatement pstmt = null;
        
        try {
            con = DBManager.getConnection();			
            pstmt = con.prepareStatement(sql);
            
            pstmt.setInt(1, sale.getCustId());
            pstmt.setInt(2, sale.getPhoneId());
            pstmt.setInt(3, sale.getQuantity());
            pstmt.setInt(4, sale.getTotal_price());
            
            ret = pstmt.executeUpdate();
            
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(pstmt, con);
        }
        
        return ret;
    }
	
	public int deleteSale(int saleId) {
	    String selectSaleSql = "SELECT quantity, phoneid FROM sales WHERE saleid = ?";
	    String deleteSaleSql = "DELETE FROM sales WHERE saleid = ?";
	    String updateStockSql = "UPDATE phones p "
	                           + "JOIN sales s ON s.phoneid = p.phoneid "
	                           + "SET p.stock = p.stock + s.quantity "
	                           + "WHERE s.saleid = ?";

	    try (Connection con = DBManager.getConnection();
	         PreparedStatement selectSaleStmt = con.prepareStatement(selectSaleSql);
	         PreparedStatement deleteSaleStmt = con.prepareStatement(deleteSaleSql);
	         PreparedStatement updateStockStmt = con.prepareStatement(updateStockSql)) {

	        // 1. 판매 데이터 조회 (quantity와 phoneid를 가져옴)
	        selectSaleStmt.setInt(1, saleId);
	        ResultSet rs = selectSaleStmt.executeQuery();
	        
	        if (rs.next()) {
	            int quantity = rs.getInt("quantity");
	            int phoneId = rs.getInt("phoneid");
	            
	            // 2. 재고 업데이트
	            updateStockStmt.setInt(1, saleId);
	            int rowsUpdated = updateStockStmt.executeUpdate();  // 재고 수정 실행
	            if (rowsUpdated > 0) {
	                System.out.println("Stock updated before deleting sale.");
	            }

	            // 3. 판매 데이터 삭제
	            deleteSaleStmt.setInt(1, saleId);
	            int rowsDeleted = deleteSaleStmt.executeUpdate();  // 판매 삭제 실행
	            if (rowsDeleted > 0) {
	                System.out.println("Sale deleted.");
	            } else {
	                System.out.println("No sale found with the given saleId.");
	            }
	            
	            return rowsDeleted;  // 삭제된 레코드 수 반환
	        } else {
	            System.out.println("No sale found with the given saleId.");
	            return 0;  // 삭제된 레코드가 없음
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return 0;  // 예외 발생 시 0 반환
	    }
	}

	
	public int updateSale(Sale sale) {
	    // 판매 정보 수정 쿼리
	    String updateSaleSql = "UPDATE sales s "
	                         + "JOIN phones p ON s.phoneid = p.phoneid "
	                         + "SET s.quantity = ?, s.total_price = ? "
	                         + "WHERE s.saleid = ?";

	    // 판매 수정 쿼리 (재고 수정)
	    String updateStockSql = "UPDATE phones p "
	                          + "JOIN sales s ON s.phoneid = p.phoneid "
	                          + "SET p.stock = p.stock - ? "
	                          + "WHERE s.saleid = ?";

	    try (Connection con = DBManager.getConnection();
	         PreparedStatement updateSaleStmt = con.prepareStatement(updateSaleSql);
	         PreparedStatement updateStockStmt = con.prepareStatement(updateStockSql)) {

	        // 판매 수정 전에 sales 테이블에서 quantity 값을 조회
	        String checkSalesSql = "SELECT quantity, phoneid FROM sales WHERE saleid = ?";
	        try (PreparedStatement checkSalesStmt = con.prepareStatement(checkSalesSql)) {
	            checkSalesStmt.setInt(1, sale.getSaleId());
	            ResultSet rs = checkSalesStmt.executeQuery();
	            if (rs.next()) {
	                int oldQuantity = rs.getInt("quantity");
	                int phoneId = rs.getInt("phoneid");

	                // 전화 가격을 조회하여 total_price 계산
	                String getPhonePriceSql = "SELECT price FROM phones WHERE phoneid = ?";
	                try (PreparedStatement getPhonePriceStmt = con.prepareStatement(getPhonePriceSql)) {
	                    getPhonePriceStmt.setInt(1, phoneId);
	                    ResultSet rsPrice = getPhonePriceStmt.executeQuery();
	                    if (rsPrice.next()) {
	                        int phonePrice = rsPrice.getInt("price");

	                        // 판매 수정 쿼리 (수정된 quantity와 계산된 total_price 반영)
	                        updateSaleStmt.setInt(1, sale.getQuantity());
	                        updateSaleStmt.setInt(2, sale.getQuantity() * phonePrice);  // quantity * phonePrice로 total_price 계산
	                        updateSaleStmt.setInt(3, sale.getSaleId());

	                        // 재고 수정을 위해 oldQuantity와 새로운 quantity 비교
	                        int quantityDifference = sale.getQuantity() - oldQuantity;
	                        updateStockStmt.setInt(1, quantityDifference);  // 재고 변화량
	                        updateStockStmt.setInt(2, sale.getSaleId());

	                        // 트랜잭션 관리
	                        con.setAutoCommit(false);  // Auto-commit 끄기 (수동으로 커밋)

	                        try {
	                            // 판매 업데이트 및 재고 업데이트 실행
	                            int rowsUpdatedSale = updateSaleStmt.executeUpdate();
	                            int rowsUpdatedStock = updateStockStmt.executeUpdate();

	                            // 트랜잭션 커밋
	                            con.commit();

	                            // 업데이트 후 상태 확인
	                            System.out.println("Sale updated. Rows affected: " + rowsUpdatedSale);
	                            System.out.println("Stock updated. Rows affected: " + rowsUpdatedStock);

	                            return rowsUpdatedSale;  // 업데이트된 판매 레코드 수 반환
	                        } catch (SQLException ex) {
	                            // 예외가 발생하면 롤백
	                            con.rollback();
	                            ex.printStackTrace();
	                            return 0;
	                        } finally {
	                            con.setAutoCommit(true);  // Auto-commit을 다시 켬
	                        }

	                    } else {
	                        System.out.println("Phone price not found");
	                        return 0;
	                    }
	                }

	            } else {
	                System.out.println("Sale not found with the given saleId.");
	                return 0;  // 해당 saleId에 대한 판매가 없음
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return 0;  // 예외 발생 시 0 반환
	    }
	}

	public int insertSaleAndUpdateStock(Sale sale) {
	    int result = insertSale(sale); // 판매 내역 추가
	    
	    if (result > 0) {
	        // 판매 내역 추가 성공 시 재고 업데이트
	        String updateStockQuery = "UPDATE phones SET stock = stock - ? WHERE phoneid = ?";
	        
	        try (Connection con = DBManager.getConnection();
	             PreparedStatement pstmt = con.prepareStatement(updateStockQuery)) {
	             
	            pstmt.setInt(1, sale.getQuantity());
	            pstmt.setInt(2, sale.getPhoneId());
	            
	            int affectedRows = pstmt.executeUpdate();
	            
	            if (affectedRows > 0) {
	                return result; // 재고 업데이트 성공
	            } else {
	                // 재고 업데이트 실패 시 롤백 처리
	                return -1;
	            }
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return -1; // 재고 업데이트 예외 발생 시 실패
	        }
	    }
	    
	    return -1; // 판매 내역 추가 실패 시
	}
	
	public List<Sale> listSale(){
		
		List<Sale> list = new ArrayList<>();
		String sql = "select * from sales; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();			
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Sale sale = new Sale();
				sale.setSaleId(rs.getInt("saleid"));
				sale.setCustId(rs.getInt("custid"));
				sale.setPhoneId(rs.getInt("phoneid"));
				sale.setQuantity(rs.getInt("quantity"));
				sale.setTotal_price(rs.getInt("total_price"));
				
				list.add(sale);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return list;
	}
	
	public List<Sale> listSale(int phoneId) throws SQLException {
	    List<Sale> sales = new ArrayList<>();
	    String query = "SELECT * FROM sales WHERE phoneId = ?"; // phoneId로 검색
	    
	    try (Connection con = DBManager.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(query)) {
	        
	        pstmt.setInt(1, phoneId); // phoneId 설정
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            int saleId = rs.getInt("saleId");
	            int custId = rs.getInt("custId");
	            int quantity = rs.getInt("quantity");
	            int totalPrice = rs.getInt("total_price");
	            sales.add(new Sale(saleId, custId, phoneId, quantity, totalPrice));
	        }
	    }
	    return sales;
	}

	
	public Sale detailSale(int saleId) {
		Sale sale = null;
		String sql = "select * from sales where saleid = ? ; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();			
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, saleId);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				sale = new Sale();
				sale.setSaleId(saleId);
				sale.setCustId(rs.getInt("custid"));
				sale.setPhoneId(rs.getInt("phoneid"));
				sale.setQuantity(rs.getInt("quantity"));
				sale.setTotal_price(rs.getInt("total_price"));
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return sale;
	}

	public List<Sale> getSalesByPhoneId(int phoneId) throws SQLException {
	    List<Sale> sales = new ArrayList<>();
	    String sql = "SELECT * FROM sales WHERE phoneId = ?;";

	    // DB 연결 객체들
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        // DB 연결 생성
	        con = DBManager.getConnection();

	        // 쿼리 준비
	        pstmt = con.prepareStatement(sql);
	        pstmt.setInt(1, phoneId);  // phoneId로 검색

	        // 쿼리 실행
	        rs = pstmt.executeQuery();

	        // 결과가 존재하면 Sale 객체 리스트에 추가
	        while (rs.next()) {
	            Sale sale = new Sale();
	            sale.setSaleId(rs.getInt("saleId"));
	            sale.setCustId(rs.getInt("custId"));
	            sale.setPhoneId(rs.getInt("phoneId"));
	            sale.setQuantity(rs.getInt("quantity"));
	            sale.setTotal_price(rs.getInt("total_price"));
	            sales.add(sale); // 리스트에 추가
	        }

	    } catch (SQLException e) {
	        // SQL 예외 처리
	        e.printStackTrace();
	        throw e;  // 예외 던짐
	    } finally {
	        // 연결 종료
	        DBManager.releaseConnection(pstmt, con);
	    }

	    return sales;
	}


}
