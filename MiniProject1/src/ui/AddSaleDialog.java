package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import dao.SaleDao;
import dao.PhoneDao;
import dto.Sale;
import dto.Phone;

public class AddSaleDialog extends JDialog {
    private JTextField custIdField, phoneIdField, quantityField, total_priceField;
    private JButton addButton;
    private SaleDao saleDao;
    private PhoneDao phoneDao;

    public AddSaleDialog(SaleManager parent, DefaultTableModel tableModel) {
        setTitle("Sale Add Dialog");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        saleDao = new SaleDao();
        phoneDao = new PhoneDao();

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        // Fields
        custIdField = new JTextField();
        phoneIdField = new JTextField();
        quantityField = new JTextField();
        total_priceField = new JTextField();
        total_priceField.setEditable(false); // total_priceField를 읽기 전용으로 설정

        // Add fields with labels
        inputPanel.add(new JLabel("Customer ID:"));
        inputPanel.add(custIdField);
        inputPanel.add(new JLabel("Phone ID:"));
        inputPanel.add(phoneIdField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Total Price:"));
        inputPanel.add(total_priceField);

        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        buttonPanel.add(addButton);

        // Add panels to dialog
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add document listeners to phoneIdField and quantityField
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTotalPrice();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTotalPrice();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTotalPrice();
            }
        };
        phoneIdField.getDocument().addDocumentListener(documentListener);
        quantityField.getDocument().addDocumentListener(documentListener);

        addButton.addActionListener(e -> {

        	String custIdText = custIdField.getText().trim();
            String phoneIdText = phoneIdField.getText().trim();
            String quantityText = quantityField.getText().trim();

            if (custIdText.isEmpty() || phoneIdText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 채워주세요.");
                return;
            }

            int custId, phoneId, quantity, total_price;
            try {
                custId = Integer.parseInt(custIdText);
                phoneId = Integer.parseInt(phoneIdText);
                quantity = Integer.parseInt(quantityText);
                total_price = Integer.parseInt(total_priceField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "모든 필드 값은 숫자여야 합니다.");
                return;
            }

            Sale newSale = new Sale(custId, phoneId, quantity, total_price);

            int result = parent.insertSale(newSale);  // SaleManager의 insertSale 호출
            if (result == 1) {
                JOptionPane.showMessageDialog(this, "판매 등록이 완료되었습니다.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "판매 등록에 실패하였습니다.");
            }
        });

        
    }

    private void updateTotalPrice() {
        String phoneIdText = phoneIdField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (!phoneIdText.isEmpty() && !quantityText.isEmpty()) {
            try {
                int phoneId = Integer.parseInt(phoneIdText);
                int quantity = Integer.parseInt(quantityText);

                Phone phone = phoneDao.detailPhone(phoneId);
                if (phone != null) {
                    int totalPrice = phone.getPrice() * quantity;
                    total_priceField.setText(String.valueOf(totalPrice));
                } else {
                    total_priceField.setText("0");
                }
            } catch (NumberFormatException ex) {
                total_priceField.setText("0");
            }
        } else {
            total_priceField.setText("0");
        }
    }
}
