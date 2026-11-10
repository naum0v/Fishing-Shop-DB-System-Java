package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Customer;
import ru.rsreu.fishingshop.model.CardLevel;
import ru.rsreu.fishingshop.model.LoyaltyCard;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class LoyaltyCardDialog extends JDialog {
	private JTextField cardNumberField, pointsField;
	private JComboBox<Customer> customerCombo;
	private JComboBox<CardLevel> levelCombo;
	private boolean confirmed = false;
	private LoyaltyCard card;
	private final DAOFactory factory;

	public LoyaltyCardDialog(Frame parent, LoyaltyCard existing, DAOFactory factory) {
		super(parent, existing == null ? "Добавить карту" : "Изменить карту", true);
		this.factory = factory;
		this.card = existing;
		setSize(1150, 300);
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
		add(new JLabel("Номер карты:"), gbc);
		gbc.gridx = 1;
		cardNumberField = new JTextField(20);
		add(cardNumberField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Начальные баллы:"), gbc);
		gbc.gridx = 1;
		pointsField = new JTextField(20);
		add(pointsField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Покупатель:"), gbc);
		gbc.gridx = 1;
		customerCombo = new JComboBox<>();
		loadCustomers();
		add(customerCombo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		add(new JLabel("Уровень:"), gbc);
		gbc.gridx = 1;
		levelCombo = new JComboBox<>();
		loadLevels();
		add(levelCombo, gbc);

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

		if (card != null) {
			cardNumberField.setText(card.cardNumber());
			pointsField.setText(card.accumulatedPoints().toString());
			
			for (int i = 0; i < customerCombo.getItemCount(); i++) {
				if (customerCombo.getItemAt(i).id().equals(card.customerId())) {
					customerCombo.setSelectedIndex(i);
					break;
				}
			}
			for (int i = 0; i < levelCombo.getItemCount(); i++) {
				if (levelCombo.getItemAt(i).id().equals(card.levelId())) {
					levelCombo.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private void loadCustomers() {
		List<Customer> customers = factory.getCustomerDAO().getAllCustomers();
		for (Customer c : customers) {
			customerCombo.addItem(c);
		}
		customerCombo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value instanceof Customer) {
					setText(((Customer) value).fullName() + " (ID=" + ((Customer) value).id() + ")");
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
	}

	private void loadLevels() {
		List<CardLevel> levels = factory.getCardLevelDAO().getAllCardLevels();
		for (CardLevel l : levels) {
			levelCombo.addItem(l);
		}
		levelCombo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value instanceof CardLevel) {
					setText(((CardLevel) value).name() + " (скидка " + ((CardLevel) value).discountPercent() + "%)");
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
	}

	private void onOk() {
		try {
			String number = cardNumberField.getText().trim();
			if (number.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Номер карты обязателен.");
				return;
			}
			java.math.BigDecimal points = new java.math.BigDecimal(pointsField.getText().trim());
			if (points.compareTo(java.math.BigDecimal.ZERO) < 0) {
				JOptionPane.showMessageDialog(this, "Баллы не могут быть отрицательными.");
				return;
			}
			Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
			CardLevel selectedLevel = (CardLevel) levelCombo.getSelectedItem();
			if (selectedCustomer == null || selectedLevel == null) {
				JOptionPane.showMessageDialog(this, "Выберите покупателя и уровень.");
				return;
			}
			if (card != null) {
				card = new LoyaltyCard(card.id(), number, LocalDate.now(), points, selectedCustomer.id(),
						selectedLevel.id());
			} else {
				card = new LoyaltyCard(null, number, LocalDate.now(), points, selectedCustomer.id(),
						selectedLevel.id());
			}
			confirmed = true;
			dispose();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Введите корректное число для баллов.");
		}
	}

	private void onCancel() {
		confirmed = false;
		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public LoyaltyCard getLoyaltyCard() {
		return card;
	}
}