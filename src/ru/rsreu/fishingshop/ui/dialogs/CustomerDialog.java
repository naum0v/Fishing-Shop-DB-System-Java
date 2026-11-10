package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.model.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerDialog extends JDialog {
	private JTextField fullNameField, phoneField, emailField, addressField;
	private boolean confirmed = false;
	private Customer customer;

	public CustomerDialog(Frame parent, Customer existing) {
		super(parent, existing == null ? "Добавить покупателя" : "Изменить покупателя", true);
		this.customer = existing;
		setSize(400, 300);
		setLocationRelativeTo(parent);
		initUI();
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("ФИО:"), gbc);
		gbc.gridx = 1;
		fullNameField = new JTextField(20);
		add(fullNameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Телефон:"), gbc);
		gbc.gridx = 1;
		phoneField = new JTextField(20);
		add(phoneField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Email:"), gbc);
		gbc.gridx = 1;
		emailField = new JTextField(20);
		add(emailField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
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

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		add(buttonPanel, gbc);

		if (customer != null) {
			fullNameField.setText(customer.fullName());
			phoneField.setText(customer.phone());
			emailField.setText(customer.email());
			addressField.setText(customer.address());
		}
	}

	private void onOk() {
		String fullName = fullNameField.getText().trim();
		String phone = phoneField.getText().trim();
		String email = emailField.getText().trim();
		String address = addressField.getText().trim();

		if (fullName.isEmpty()) {
			JOptionPane.showMessageDialog(this, "ФИО обязательно.");
			return;
		}

		if (customer != null) {
			customer = new Customer(customer.id(), fullName, phone, email, address);
		} else {
			customer = new Customer(null, fullName, phone, email, address);
		}
		confirmed = true;
		dispose();
	}

	private void onCancel() {
		confirmed = false;
		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public Customer getCustomer() {
		return customer;
	}
}