package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dto.Sale;

public class EditSaleDialog extends JDialog {
	private JTextField custIdField, phoneIdField, quantityField, total_priceField;
	private JButton updateButton, deleteButton;
	
	public EditSaleDialog(SaleManager parent, DefaultTableModel tableModel, int rowIndex) { // 선택된 row index
		setTitle("Sale Edit Dialog");
		setSize(300, 200);
		setLayout(new BorderLayout());
		setLocationRelativeTo(parent);
		
		// 선택된 행(rowIndex)에서 saleid 가져오기
		Integer saleid = (Integer) tableModel.getValueAt(rowIndex, 0);
		if (saleid == null) {
		    JOptionPane.showMessageDialog(this, "잘못된 Sale ID입니다.", "에러", JOptionPane.ERROR_MESSAGE);
		    dispose();
		    return;
		}

		Sale sale = parent.detailSale(saleid);
		if (sale == null) {
		    JOptionPane.showMessageDialog(this, "해당 Sale 정보를 찾을 수 없습니다.", "에러", JOptionPane.ERROR_MESSAGE);
		    dispose();
		    return;
		}
		
		// input panel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(4, 2));
		
		// field
		custIdField = new JTextField(String.valueOf(sale.getCustId()));
		custIdField.setEditable(false);
		phoneIdField = new JTextField(String.valueOf(sale.getPhoneId()));
		phoneIdField.setEditable(false);
		quantityField = new JTextField(String.valueOf(sale.getQuantity()));
		total_priceField = new JTextField(String.valueOf(sale.getTotal_price()));
		
		// add field with label, button
		inputPanel.add(new JLabel("Cust Id"));
		inputPanel.add(custIdField);
		inputPanel.add(new JLabel("Phone Id"));
		inputPanel.add(phoneIdField);
		inputPanel.add(new JLabel("Quantity"));
		inputPanel.add(quantityField);
		inputPanel.add(new JLabel("Total Price"));
		inputPanel.add(total_priceField);
		
		// button panel
		JPanel buttonPanel = new JPanel();
		
		// button
		updateButton = new JButton("수정");
		deleteButton = new JButton("삭제");

		buttonPanel.add(updateButton);
		buttonPanel.add(deleteButton);
		
		// add inputPanel, buttonPanel to Dialog
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		// update, delete button actionListner
		updateButton.addActionListener( e -> {
			
        	int ret = JOptionPane.showConfirmDialog(this, "수정할까요?", "수정 확인", JOptionPane.YES_NO_OPTION);
        	if( ret == JOptionPane.YES_OPTION ) {
        		try {
        			int custid = Integer.parseInt(custIdField.getText());
            		int phoneid = Integer.parseInt(phoneIdField.getText());
            		int quantity = Integer.parseInt(quantityField.getText());
            		int total_price = Integer.parseInt(total_priceField.getText());
            		
            		parent.updateSale(new Sale(saleid, custid, phoneid, quantity, total_price)); // 위쪽에 선언된(선택된 row에서) 변수를 사용
            		dispose();
        		} catch(NumberFormatException ex) {
        			JOptionPane.showMessageDialog(this, "입력값이 올바르지 않습니다. 숫자를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        		}
        	}
		});
		
		deleteButton.addActionListener(e -> {
		    int ret = JOptionPane.showConfirmDialog(this, "삭제할까요?", "삭제 확인", JOptionPane.YES_NO_OPTION);
		    if (ret == JOptionPane.YES_OPTION) {
		        parent.deleteSale(saleid);  // saleid를 직접 사용하여 삭제
		        dispose();
		    }
		});

	}
}

