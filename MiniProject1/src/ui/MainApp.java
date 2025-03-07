package ui;

import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // SaleManager와 PhoneManager 인스턴스화 후 표시
            new SaleManager().setVisible(true);
            new PhoneManager().setVisible(true);
        });
    }
}

