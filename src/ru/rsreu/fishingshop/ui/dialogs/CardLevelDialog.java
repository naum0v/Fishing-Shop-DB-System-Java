package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.model.CardLevel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CardLevelDialog extends JDialog {
	private JTextField nameField, discountField, multiplierField;
	private boolean confirmed = false;
	private CardLevel level;

	public CardLevelDialog(Frame parent, CardLevel existing) {
		super(parent, existing == null ? "Добавить уровень карты" : "Изменить уровень карты", true);
		this.level = existing;
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
		add(new JLabel("Название:"), gbc);
		gbc.gridx = 1;
		nameField = new JTextField(20);
		add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Скидка %:"), gbc);
		gbc.gridx = 1;
		discountField = new JTextField(20);
		add(discountField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Коэффициент баллов:"), gbc);
		gbc.gridx = 1;
		multiplierField = new JTextField(20);
		add(multiplierField, gbc);

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

		if (level != null) {
			nameField.setText(level.name());
			discountField.setText(level.discountPercent().toString());
			multiplierField.setText(level.bonusMultiplier().toString());
		}
	}

	private void onOk() {
		try {
			String name = nameField.getText().trim();
			if (name.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Название обязательно.");
				return;
			}
			BigDecimal discount = new BigDecimal(discountField.getText().trim());
			BigDecimal multiplier = new BigDecimal(multiplierField.getText().trim());
			if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(new BigDecimal("100")) > 0) {
				JOptionPane.showMessageDialog(this, "Скидка должна быть от 0 до 100.");
				return;
			}
			if (multiplier.compareTo(BigDecimal.ZERO) <= 0) {
				JOptionPane.showMessageDialog(this, "Коэффициент должен быть положительным.");
				return;
			}
			if (level != null) {
				level = new CardLevel(level.id(), name, discount, multiplier);
			} else {
				level = new CardLevel(null, name, discount, multiplier);
			}
			confirmed = true;
			dispose();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Введите корректные числовые значения.");
		}
	}

	private void onCancel() {
		confirmed = false;
		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public CardLevel getCardLevel() {
		return level;
	}
}