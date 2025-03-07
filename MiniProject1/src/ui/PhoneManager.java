package ui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import dto.Phone;

public class PhoneManager extends JFrame { 

	private JTable table; // grid ui component
	private DefaultTableModel tableModel;// grid data
	private JButton searchButton, resetButton, addButton, editButton, deleteButton, listButton;
	private JTextField searchWordField;

	private PhoneDao phoneDao;
	private SaleManager saleManager;
	
	public PhoneManager() {
		// 화면 UI 와 관련된 설정
		setTitle("Phone Manager");
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		phoneDao = new PhoneDao();
		
		// table
		tableModel = new DefaultTableModel(new Object[] {"Phone ID", "Brand", "Model", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are not editable
            }
		};
		table = new JTable(tableModel);
		
		listPhone();
		
		// search
		Dimension  textFieldSize = new Dimension(400, 28);
		searchWordField = new JTextField();
		searchWordField.setPreferredSize(textFieldSize);
		
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
		listButton = new JButton("판매 내역");
		
		// button 2개를 담는 JPanel 객체를 만들고 그 객체를 PhoneManager에 담는다.
		JPanel buttonPanel = new JPanel(); // default layout : Flow Layout
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(listButton);
		
		// table, buttonPanel 을 PhoneManager 에 붙인다. 
		// PhoneManager 의 layout 에 따라 결정
		
		// PhoneManager 의 layout 설정 
		setLayout(new BorderLayout());
		add(searchPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		// button action event 처리
		searchButton.addActionListener( e -> {
			String searchWord = searchWordField.getText();
			if( !searchWord.isBlank() ) {
				listPhone(searchWord);
			} 
		});
		
		// 초기화 버튼 클릭 
		resetButton.addActionListener( e -> {
			searchWordField.setText("");
			listPhone();
		});
		
		addButton.addActionListener( e -> {
			// AddBookDialog 를 띄운다.
			AddPhoneDialog addDialog = new AddPhoneDialog(this, this.tableModel);
			addDialog.setVisible(true);
		}); 
		
		editButton.addActionListener(e -> {
			// table 에 선택된 row 가 있으면 EditBookDialog 를 띄운다.
			// table 에 선택된 row
			int selectedRow = table.getSelectedRow();
			if( selectedRow >= 0 ) {
				EditPhoneDialog editDialog = new EditPhoneDialog(this, this.tableModel, selectedRow);
				editDialog.setVisible(true);
			}else {
				JOptionPane.showMessageDialog(this, "핸드폰을 선택하세요.");
			}
			
		});
		
		listButton.addActionListener( e -> {
			SaleManager saleManager = new SaleManager();
			saleManager.setVisible(true);
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
            	// double click
            	if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        EditPhoneDialog editDialog = new EditPhoneDialog(PhoneManager.this, tableModel, selectedRow);
                        editDialog.setVisible(true);
                    }
            	}

            }
		});

	}
	
	public void refreshPhoneTable() {
        clearTable();
        listPhone(); // 재고를 갱신하는 메소드 호출
    }
	
	private void clearTable() {
		tableModel.setRowCount(0);
	}
	
	private void listPhone() {
		// 현재 tableModel 을 정리하고 
		clearTable();
		
		List<Phone> phoneList = phoneDao.listPhone();
		
		for (Phone phone : phoneList) {
			tableModel.addRow(new Object[] {phone.getPhoneId(), phone.getBrand(), phone.getModel(), phone.getPrice(), phone.getStock() });
		}
	}
	
	private void listPhone(String searchWord) {
		// 현재 tableModel 을 정리하고 
		clearTable();
		
		List<Phone> phoneList = phoneDao.listPhone(searchWord);
		
		for (Phone phone : phoneList) {
			tableModel.addRow(new Object[] {phone.getPhoneId(), phone.getBrand(), phone.getModel(), phone.getPrice(), phone.getStock() });
		}
	}
	
	Phone detailPhone(int phoneId) {
		return phoneDao.detailPhone(phoneId);
	}
	
	int insertPhone(Phone phone) {
		int ret = phoneDao.insertPhone(phone);
		if( ret == 1 ) {
			listPhone();
		}
		return ret;
	}
	
	void updatePhone(Phone phone) {
		int ret = phoneDao.updatePhone(phone);
		if( ret == 1 ) {
			listPhone();
			((DefaultTableModel) table.getModel()).fireTableDataChanged();
		}
	}
	
	int deletePhone(int phoneId) {
		int ret = phoneDao.deletePhone(phoneId);
		if( ret == 1 ) {
			listPhone();
		}
		return ret;
	}

}
