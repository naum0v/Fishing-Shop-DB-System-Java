package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.LoyaltyCard;
import ru.rsreu.fishingshop.ui.dialogs.LoyaltyCardDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class LoyaltyCardPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public LoyaltyCardPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "Номер карты", "Дата регистрации", "Баллы", "Покупатель ID", "Уровень ID" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void initButtons() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton addBtn = new JButton("Добавить");
		JButton editBtn = new JButton("Изменить");
		JButton deleteBtn = new JButton("Удалить");
		JButton refreshBtn = new JButton("Обновить");
		JButton spendBtn = new JButton("Списать баллы");

		addBtn.addActionListener(this::onAdd);
		editBtn.addActionListener(this::onEdit);
		deleteBtn.addActionListener(this::onDelete);
		refreshBtn.addActionListener(e -> loadData());
		spendBtn.addActionListener(this::onSpend);

		buttonPanel.add(addBtn);
		buttonPanel.add(editBtn);
		buttonPanel.add(deleteBtn);
		buttonPanel.add(refreshBtn);
		buttonPanel.add(spendBtn);
		add(buttonPanel, BorderLayout.NORTH);
	}

	private void onAdd(ActionEvent e) {
		LoyaltyCardDialog dialog = new LoyaltyCardDialog((Frame) SwingUtilities.getWindowAncestor(this), null, factory);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getLoyaltyCardDAO().addLoyaltyCard(dialog.getLoyaltyCard());
			loadData();
		}
	}

	private void onEdit(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите запись для редактирования.");
			return;
		}
		Long cardId = (Long) tableModel.getValueAt(selectedRow, 0);
		String levelStr = JOptionPane.showInputDialog(this, "Введите новый level_id:");
		if (levelStr == null)
			return;
		try {
			Long newLevelId = Long.parseLong(levelStr);
			factory.getLoyaltyCardDAO().updateLoyaltyCard(cardId, newLevelId);
			loadData();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Введите корректное число.");
		} catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
		}
	}

	private void onDelete(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите запись для удаления.");
			return;
		}
		Long cardId = (Long) tableModel.getValueAt(selectedRow, 0);
		int confirm = JOptionPane.showConfirmDialog(this, "Удалить карту ID=" + cardId + "?", "Подтверждение",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				factory.getLoyaltyCardDAO().deleteLoyaltyCard(cardId);
				loadData();
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
			}
		}
	}

	private void onSpend(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите карту.");
			return;
		}
		Long customerIdFromTable = (Long) tableModel.getValueAt(selectedRow, 4);
		String pointsStr = JOptionPane.showInputDialog(this, "Введите количество баллов для списания:");
		if (pointsStr == null)
			return;
		try {
			java.math.BigDecimal points = new java.math.BigDecimal(pointsStr);
			factory.getLoyaltyCardDAO().applyBonusPoints(customerIdFromTable, points);
			loadData();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Введите корректное число.");
		} catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
		}
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<LoyaltyCard> cards = factory.getLoyaltyCardDAO().getAllLoyaltyCards();
		for (LoyaltyCard card : cards) {
			tableModel.addRow(new Object[] { card.id(), card.cardNumber(), card.registrationDate(),
					card.accumulatedPoints(), card.customerId(), card.levelId() });
		}
	}
}