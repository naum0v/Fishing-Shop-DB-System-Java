package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.model.Supplier;

import javax.swing.*;
import java.awt.*;

public class SupplierDialog extends JDialog {
    private JTextField companyField, contactField, phoneField, addressField;
    private boolean confirmed = false;
    private Supplier supplier;

    public SupplierDialog(Frame parent, Supplier existing) {
        super(parent, existing == null ? "Добавить поставщика" : "Изменить поставщика", true);
        this.supplier = existing;
        setSize(450, 250);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Компания:"), gbc);
        gbc.gridx = 1;
        companyField = new JTextField(20);
        add(companyField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Контактное лицо:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(20);
        add(addressField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Отмена");
        okBtn.addActionListener(e -> onOk());
        cancelBtn.addActionListener(e -> onCancel());
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        if (supplier != null) {
            companyField.setText(supplier.companyName());
            contactField.setText(supplier.contactPerson());
            phoneField.setText(supplier.phone());
            addressField.setText(supplier.address());
        }
    }

    private void onOk() {
        String company = companyField.getText().trim();
        String contact = contactField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        if (company.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Название компании обязательно.");
            return;
        }
        if (supplier != null) {
            supplier = new Supplier(supplier.id(), company, contact, phone, address);
        } else {
            supplier = new Supplier(null, company, contact, phone, address);
        }
        confirmed = true;
        dispose();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Supplier getSupplier() { return supplier; }
}