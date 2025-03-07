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

import dto.Phone;

public class EditPhoneDialog extends JDialog {
	private JTextField phoneIdField, brandField, modelField, priceField, stockField;
	private JButton updateButton, deleteButton;
	
	public EditPhoneDialog(PhoneManager parent, DefaultTableModel tableModel, int rowIndex) { // 선택된 row index
		setTitle("Phone Edit Dialog");
		setSize(300, 200);
		setLayout(new BorderLayout());
		setLocationRelativeTo(parent); // 부모에 맞게
		
		// 선택된 phone 의 phoneId 로 phones table 에서 조회
		Integer phoneId = (Integer) tableModel.getValueAt(rowIndex, 0);
		Phone phone = parent.detailPhone(phoneId);
		
		// input panel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(5, 2));
		
		// field
		phoneIdField = new JTextField(String.valueOf(phoneId));
		phoneIdField.setEditable(false);
		brandField = new JTextField(phone.getBrand());
		modelField = new JTextField(phone.getModel());
		priceField = new JTextField(String.valueOf(phone.getPrice()));
		stockField = new JTextField(String.valueOf(phone.getStock()));
		
		// add field with label, button
		inputPanel.add(new JLabel("Phone Id"));
		inputPanel.add(phoneIdField);
		inputPanel.add(new JLabel("Brand"));
		inputPanel.add(brandField);
		inputPanel.add(new JLabel("Model"));
		inputPanel.add(modelField);
		inputPanel.add(new JLabel("Price"));
		inputPanel.add(priceField);
		inputPanel.add(new JLabel("stock"));
		inputPanel.add(stockField);
		
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
		
		
		deleteButton.addActionListener(e -> {
		    int ret = JOptionPane.showConfirmDialog(this, "삭제할까요?", "삭제 확인", JOptionPane.YES_NO_OPTION);
		    if (ret == JOptionPane.YES_OPTION) {
		        // 삭제 처리
		        int result = parent.deletePhone(phoneId); // 반환값을 받음

		        if (result == 1) {
		            // 삭제 성공 메시지
		            JOptionPane.showMessageDialog(this, "휴대폰 정보가 삭제되었습니다.", "삭제 성공", JOptionPane.INFORMATION_MESSAGE);
		        } else {
		            // 삭제 실패 메시지
		            JOptionPane.showMessageDialog(this, "판매내역이 존재하므로 삭제할 수 없습니다.", "삭제 실패", JOptionPane.ERROR_MESSAGE);
		        }
		        dispose(); // 다이얼로그 닫기
		    }
		});

		updateButton.addActionListener(e -> {
		    int ret = JOptionPane.showConfirmDialog(this, "수정할까요?", "수정 확인", JOptionPane.YES_NO_OPTION);
		    if (ret == JOptionPane.YES_OPTION) {
		        String brand = brandField.getText();
		        String model = modelField.getText();
		        int price = Integer.parseInt(priceField.getText());
		        int stock = Integer.parseInt(stockField.getText());

		        parent.updatePhone(new Phone(phoneId, brand, model, price, stock));

		        // 수정 성공 메시지
		        JOptionPane.showMessageDialog(this, "휴대폰 정보가 수정되었습니다.", "수정 성공", JOptionPane.INFORMATION_MESSAGE);
		        dispose(); // 다이얼로그 닫기
		    }
		});

	}
}

