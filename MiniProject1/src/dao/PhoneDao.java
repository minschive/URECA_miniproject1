package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.DBManager;
import dto.Phone;

public class PhoneDao {
	
	public boolean isModelExists(String model) {
		String sql = "select count(*) from phones where model = ? ";
		try(Connection con = DBManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){
				pstmt.setString(1, model);
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()) {
						int count = rs.getInt(1);
						return count > 0;
					}
				}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int insertPhone(Phone phone) {
		int ret = -1;
		String sql = "insert into phones (brand, model, price, stock) values ( ?, ?, ?, ? ); ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = DBManager.getConnection();			
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, phone.getBrand());
			pstmt.setString(2, phone.getModel());
			pstmt.setInt(3, phone.getPrice());
			pstmt.setInt(4,  phone.getStock());
			
			ret = pstmt.executeUpdate();
			
			if(ret > 0) {
				try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
					if(generatedKeys.next()) {
						phone.setPhoneId(generatedKeys.getInt(1));
					}
				}
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return ret;
	}
	
	public int updatePhone(Phone phone) {
		int ret = -1;		
		String sql = "update phones set brand = ?, model = ?, price = ?, stock = ? where phoneid = ?; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = DBManager.getConnection();			
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, phone.getBrand());
			pstmt.setString(2, phone.getModel());
			pstmt.setInt(3, phone.getPrice());
			pstmt.setInt(4, phone.getStock());
			pstmt.setInt(5,  phone.getPhoneId());
			
			ret = pstmt.executeUpdate();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return ret;
	}
	
	public int updateStock(int phoneId, int newStock) {
	    String sql = "UPDATE phones SET stock = ? WHERE phoneid = ?";
	    Connection con = null;
	    PreparedStatement pstmt = null;

	    try {
	        con = DBManager.getConnection(); // DB 연결
	        pstmt = con.prepareStatement(sql); // PreparedStatement 준비

	        pstmt.setInt(1, newStock); // 새로운 stock 값을 설정
	        pstmt.setInt(2, phoneId);  // phoneId 값을 설정
	        
	        return pstmt.executeUpdate(); // 실행 후, 성공 시 1 반환, 실패 시 0 반환
	    } catch (SQLException e) {
	        e.printStackTrace(); // 예외 출력
	        return 0; // 예외 발생 시 0 반환
	    } finally {
	        DBManager.releaseConnection(pstmt, con); // 연결과 Statement 자원 해제
	    }
	}


	public int decreaseStock(int phoneid, int quantity) {
        String sql = "UPDATE phones SET stock = stock - ? WHERE phoneid = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBManager.getConnection(); // DB 연결
            pstmt = con.prepareStatement(sql); // PreparedStatement 준비

            pstmt.setInt(1, quantity); // quantity를 첫 번째 파라미터로 설정
            pstmt.setInt(2, phoneid);  // phoneid를 두 번째 파라미터로 설정
            
            return pstmt.executeUpdate(); // 실행 후, 성공 시 1 반환, 실패 시 0 반환
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 출력
            return 0; // 예외 발생 시 0 반환
        } finally {
            DBManager.releaseConnection(pstmt, con); // 연결과 Statement 자원 해제
        }
    }

	
	public int deletePhone(int phoneId) {
	    int ret = -1;  // 기본적으로 삭제 실패를 나타내는 값
	    String sql = "SELECT COUNT(*) FROM sales WHERE phoneid = ?";
	    
	    Connection con = DBManager.getConnection();
	    PreparedStatement pstmt = null;
	    PreparedStatement deleteStmt = null;
	    ResultSet rs = null;

	    try {
	        // 연결된 connection 객체가 이미 존재한다고 가정
	        pstmt = con.prepareStatement(sql);
	        pstmt.setInt(1, phoneId);
	        rs = pstmt.executeQuery();

	        if (rs.next() && rs.getInt(1) > 0) {
	            // 판매내역이 있으면 삭제할 수 없음
	            ret = -1; // 판매내역 존재로 실패
	        } else {
	            // 판매내역이 없으면 phones 테이블에서 삭제
	            String deletePhoneQuery = "DELETE FROM phones WHERE phoneid = ?";
	            deleteStmt = con.prepareStatement(deletePhoneQuery);
	            deleteStmt.setInt(1, phoneId);
	            int rowsAffected = deleteStmt.executeUpdate();
	            
	            if (rowsAffected > 0) {
	                ret = 1;  // 삭제 성공
	            } else {
	                ret = -1;  // 삭제 실패
	            }
	        }
	    } catch (SQLException e) {
	        // SQL 예외 처리
	        e.printStackTrace();
	    } finally {
	        // 자원 해제
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	            if (pstmt != null) {
	                pstmt.close();
	            }
	            if (deleteStmt != null) {
	                deleteStmt.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return ret;  // 삭제 성공 시 1, 실패 시 -1 반환
	}

	
	public List<Phone> listPhone(){
		List<Phone> list = new ArrayList<>();
		
		String sql = "select * from phones; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();			
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Phone phone = new Phone();
				phone.setPhoneId(rs.getInt("phoneid"));
				phone.setBrand(rs.getString("brand"));
				phone.setModel(rs.getString("model"));
				phone.setPrice(rs.getInt("price"));
				phone.setStock(rs.getInt("stock"));
				list.add(phone);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return list;
	}
	
	public List<Phone> listPhone(String searchWord){
		List<Phone> list = new ArrayList<>();
		
		String sql = "select * from phones where brand like ? or model like ?; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%" + searchWord + "%");
			pstmt.setString(2, "%" + searchWord + "%");
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Phone phone = new Phone();
				phone.setPhoneId(rs.getInt("phoneid"));
				phone.setBrand(rs.getString("brand"));
				phone.setModel(rs.getString("model"));
				phone.setPrice(rs.getInt("price"));
				phone.setStock(rs.getInt("stock"));
				list.add(phone);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return list;
	}
	
	public Phone detailPhone(int phoneId) {
		Phone phone = null;
		
		String sql = "select * from phones where phoneid = ?; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DBManager.getConnection();			
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, phoneId);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				phone = new Phone();
				phone.setPhoneId(rs.getInt("phoneid"));
				phone.setBrand(rs.getString("brand"));
				phone.setModel(rs.getString("model"));
				phone.setPrice(rs.getInt("price"));
				phone.setStock(rs.getInt("stock"));
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return phone;
	}
}
