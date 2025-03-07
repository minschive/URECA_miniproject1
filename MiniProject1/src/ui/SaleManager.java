package ui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame; // windows application
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import dao.PhoneDao;
import dao.SaleDao;
import dto.Phone;
import dto.Sale;

public class SaleManager extends JFrame { 

	private JTable table; 
	private DefaultTableModel tableModel;
	private JButton searchButton, resetButton, addButton, editButton, deleteButton;
	private JTextField searchWordField, totalPriceField;

	private SaleDao saleDao;
	private PhoneDao phoneDao;
	
	public SaleManager() {
		// 화면 UI 와 관련된 설정
		setTitle("Sale Manager");
		setSize(600, 400);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		this.saleDao = new SaleDao();
        this.phoneDao = new PhoneDao();
		
		// table
		tableModel = new DefaultTableModel(new Object[] {"Sale ID", "Cust ID", "Phone ID", "Quantity", "Total Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are not editable
            }
		};
		table = new JTable(tableModel);
		
		listSale();
		
		// search
		Dimension  textFieldSize = new Dimension(400, 28);
		searchWordField = new JTextField();
		searchWordField.setPreferredSize(textFieldSize);
		totalPriceField = new JTextField();
		totalPriceField.setPreferredSize(textFieldSize);
		totalPriceField.setEditable(false);
		
		searchButton = new JButton("검색");
		resetButton = new JButton("초기화");
		
		JPanel searchPanel = new JPanel();
		searchPanel.add(new JLabel("검색"));
		searchPanel.add(searchWordField);
		searchPanel.add(searchButton);
		searchPanel.add(resetButton);
		
		// button
		addButton = new JButton("등록");
		editButton = new JButton("수정/삭제");
		deleteButton = new JButton("삭제");
		
		// button 2개를 담는 JPanel 객체를 만들고 그 객체를 PhoneManager에 담는다.
		JPanel buttonPanel = new JPanel(); // default layout : Flow Layout
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		
		// SaleManager 의 layout 설정 
		setLayout(new BorderLayout());
		add(searchPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER); // table < scroll pane < jframe
		add(buttonPanel, BorderLayout.SOUTH);
		
		// button action event 처리
		searchButton.addActionListener(e -> {
		    String searchWord = searchWordField.getText().trim();
		    
		    if (searchWord.isEmpty()) {
		        JOptionPane.showMessageDialog(this, "Phone ID를 입력해주세요.");
		        return;
		    }
		    
		    try {
		        int searchWordInt = Integer.parseInt(searchWord); // searchWord를 int로 변환
		        List<Sale> sales = saleDao.getSalesByPhoneId(searchWordInt); // sales 조회
		        updateSalesTable(sales); // 테이블 업데이트
		    } catch (NumberFormatException ex) {
		        // NumberFormatException 처리: 입력이 숫자가 아닐 경우
		        JOptionPane.showMessageDialog(this, "Phone ID는 숫자여야 합니다.");
		    } catch (SQLException ex) {
		        // SQL 예외 처리
		        JOptionPane.showMessageDialog(this, "판매 정보 검색 실패: " + ex.getMessage());
		    }
		});

		
		// 초기화 버튼 클릭 
		resetButton.addActionListener( e -> {
			searchWordField.setText("");
			listSale();
		});
		
		addButton.addActionListener( e -> {
			AddSaleDialog addDialog = new AddSaleDialog(this, this.tableModel);
			addDialog.setVisible(true);
		}); 
		
		editButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if( selectedRow >= 0 ) {
				EditSaleDialog editDialog = new EditSaleDialog(this, this.tableModel, selectedRow);
				editDialog.setVisible(true);
			}else {
				JOptionPane.showMessageDialog(this, "고객을 선택하세요.");
			}
			
		});
		
		
		table.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
            	// double click
            	if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        EditSaleDialog editDialog = new EditSaleDialog(SaleManager.this, tableModel, selectedRow);
                        editDialog.setVisible(true);
                    }
            	}

            }
		});
	}

	private void clearTable() {
		tableModel.setRowCount(0);
	}
	
	public void displaySalesByPhoneId(int phoneId) {
	    // 테이블 초기화
	    clearTable();  // 기존 데이터 삭제

	    try {
	        // phoneId에 해당하는 판매 내역을 가져오는 DAO 메소드 호출
	        List<Sale> sales = saleDao.getSalesByPhoneId(phoneId);

	        // 테이블에 판매 내역 추가
	        for (Sale sale : sales) {
	            tableModel.addRow(new Object[] {
	                sale.getSaleId(),
	                sale.getCustId(),
	                sale.getPhoneId(),
	                sale.getQuantity(),
	                sale.getTotal_price()
	            });
	        }

	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(this, "판매 내역 조회 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
	    }
	}

	
	// 검색된 데이터를 테이블에 표시하는 메소드 예시
	private void updateSalesTable(List<Sale> sales) {
	    DefaultTableModel model = (DefaultTableModel)table.getModel();
	    model.setRowCount(0); // 기존 데이터 지우기
	    
	    for (Sale sale : sales) {
	        model.addRow(new Object[] {
	            sale.getSaleId(), sale.getCustId(), sale.getPhoneId(),
	            sale.getQuantity(), sale.getTotal_price()
	        });
	    }
	}
	
	private void listSale() {
		clearTable();
		
		List<Sale> saleList = saleDao.listSale();
		System.out.println("Sales Data : " + saleList);
		
		SwingUtilities.invokeLater(() -> {
			for (Sale sale : saleList) {
				tableModel.addRow(new Object[] {sale.getSaleId(), sale.getCustId(), sale.getPhoneId(), sale.getQuantity(), sale.getTotal_price() });
			}
		});
		
		tableModel.fireTableDataChanged();
	}
	
	Sale detailSale(int saleId) {
	    return saleDao.detailSale(saleId);
	}
	
	void deleteSale(Integer saleId) {
	    // saleId만 사용하여 삭제하도록 변경
	    int ret = saleDao.deleteSale(saleId);  // saleId를 넘겨줍니다.
	    if (ret == 1) {
	        listSale();  // 삭제 후 갱신
	    }
	}


	public int insertSale(Sale sale) {
        // 1. 판매 내역을 sales 테이블에 삽입
        int ret = saleDao.insertSale(sale);
//        System.out.println("insertSale 반환값: " + ret);  // 반환값 확인

        if (ret == 1) {  // 판매 내역 등록 성공
            // 2. 재고 감소 작업
            int decreaseStockResult = phoneDao.decreaseStock(sale.getPhoneId(), sale.getQuantity());
//            System.out.println("재고 감소 결과: " + decreaseStockResult);  // 결과 확인
            
            if (decreaseStockResult == 1) {
                // 3. 재고 감소 성공 시 판매 내역 새로고침
               SwingUtilities.invokeLater(() -> listSale());  // 판매 내역 새로고침
            } else {
                // 재고 감소 실패 시
                JOptionPane.showMessageDialog(this, "재고 감소에 실패했습니다.");
            }
        } else {
            // 판매 내역 등록 실패 시
            JOptionPane.showMessageDialog(this, "판매 내역 등록에 실패했습니다.");
        }
        return ret;
    }

	
	void updateSale(Sale sale) {
	    if (sale == null || sale.getSaleId() == 0) {
	        System.out.println("잘못된 Sale 데이터: " + sale);
	        return;
	    }
	    
	    int ret = saleDao.updateSale(sale);
	    if (ret == 1) {
	        listSale();  // UI 갱신
	    } else {
	        System.out.println("판매 정보 업데이트 실패");
	    }
	}

	public void updateStock(int phoneId, int quantityDifference) throws SQLException {
	    // phoneId에 해당하는 stock 값을 가져옵니다.
	    Phone phone = phoneDao.detailPhone(phoneId);
	    
	    if (phone != null) {
	        int newStock = phone.getStock() + quantityDifference; // quantityDifference가 증가나 감소의 값을 나타냄

	        if (newStock < 0) {
	            // 재고가 0보다 적을 수 없으므로, 재고가 0보다 작은 경우 예외 처리 또는 메시지 출력
	            throw new SQLException("재고가 부족합니다.");
	        }

	        // stock 값을 업데이트합니다.
	        phoneDao.updateStock(phoneId, newStock);  // phoneId와 새로운 stock 값을 DB에 반영
	    } else {
	        throw new SQLException("해당 phoneId에 대한 정보가 없습니다.");
	    }
	}


}
