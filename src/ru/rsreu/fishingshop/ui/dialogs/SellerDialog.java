package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.model.Seller;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SellerDialog extends JDialog {
	private JTextField fullNameField, phoneField, hireDateField;
	private boolean confirmed = false;
	private Seller seller;

	public SellerDialog(Frame parent, Seller existing) {
		super(parent, existing == null ? "Добавить продавца" : "Изменить продавца", true);
		this.seller = existing;
		setSize(400, 250);
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
		add(new JLabel("Дата найма (YYYY-MM-DD):"), gbc);
		gbc.gridx = 1;
		hireDateField = new JTextField(20);
		add(hireDateField, gbc);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton okBtn = new JButton("OK");
		JButton cancelBtn = new JButton("Отмена");
		okBtn.addActionListener(e -> onOk());
		cancelBtn.addActionListener(e -> onCancel());
		buttonPanel.add(okBtn);
		buttonPanel.add(cancelBtn);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		add(buttonPanel, gbc);

		if (seller != null) {
			fullNameField.setText(seller.fullName());
			phoneField.setText(seller.phone());
			hireDateField.setText(seller.hireDate().toString());
		}
	}

	private void onOk() {
		try {
			String fullName = fullNameField.getText().trim();
			String phone = phoneField.getText().trim();
			String hireDateStr = hireDateField.getText().trim();
			if (fullName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "ФИО обязательно.");
				return;
			}
			LocalDate hireDate = LocalDate.parse(hireDateStr);
			if (seller != null) {
				seller = new Seller(seller.id(), fullName, phone, hireDate);
			} else {
				seller = new Seller(null, fullName, phone, hireDate);
			}
			confirmed = true;
			dispose();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Неверный формат даты. Используйте YYYY-MM-DD.");
		}
	}

	private void onCancel() {
		confirmed = false;
		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public Seller getSeller() {
		return seller;
	}
}