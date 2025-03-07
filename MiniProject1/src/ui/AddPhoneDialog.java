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

import dao.PhoneDao;
import dto.Phone;

public class AddPhoneDialog extends JDialog {
	private JTextField brandField, modelField, priceField, stockField;
	private JButton addButton;
	private PhoneDao phoneDao;
	
	public AddPhoneDialog(PhoneManager parent, DefaultTableModel tableModel) {
		setTitle("Phone Add Dialog");
		setSize(400, 300);
		setLayout(new BorderLayout());
		setLocationRelativeTo(parent); // 부모에 맞게
		
		phoneDao = new PhoneDao();
		
		// input panel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(4, 2));
				
		// field
		brandField = new JTextField();
		modelField = new JTextField();
		priceField = new JTextField();
		stockField = new JTextField();
		
		// add field with label, button
		inputPanel.add(new JLabel("Brand"));
		inputPanel.add(brandField);
		inputPanel.add(new JLabel("Model"));
		inputPanel.add(modelField);
		inputPanel.add(new JLabel("Price"));
		inputPanel.add(priceField);
		inputPanel.add(new JLabel("Stock"));
		inputPanel.add(stockField);
				
		// button panel
		JPanel buttonPanel = new JPanel();
				
		// button
		addButton = new JButton("Add");
				
		buttonPanel.add(addButton);		

		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		addButton.addActionListener( e -> {
			String brand = brandField.getText();
			String model = modelField.getText();
			String priceText = priceField.getText().trim();
			String stockText = stockField.getText().trim();
			
			if(brand.isEmpty() || model.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
				JOptionPane.showMessageDialog(this, "모든 필드를 채워주세요.");
				return;
			}
			
			int price, stock;
			try {
				price = Integer.parseInt(priceText);
				stock = Integer.parseInt(stockText);
			} catch(NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Price와 Stock은 숫자여야 합니다.");
				return;
			}
			
			// 모델명이 이미 존재하는지 확인
			if(phoneDao.isModelExists(model)) {
				JOptionPane.showMessageDialog(this, "이미 존재하는 휴대폰입니다.");
				return;
			}
			
			Phone newPhone = new Phone(0, brand, model, price, stock);
			
			int generatedPhoneId = parent.insertPhone(newPhone);
			
			dispose();
		});
	}
}
