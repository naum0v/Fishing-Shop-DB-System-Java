package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.CardLevel;
import ru.rsreu.fishingshop.ui.dialogs.CardLevelDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CardLevelPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public CardLevelPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "Название", "Скидка %", "Коэффициент баллов" };
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

		addBtn.addActionListener(this::onAdd);
		editBtn.addActionListener(this::onEdit);
		deleteBtn.addActionListener(this::onDelete);
		refreshBtn.addActionListener(e -> loadData());

		buttonPanel.add(addBtn);
		buttonPanel.add(editBtn);
		buttonPanel.add(deleteBtn);
		buttonPanel.add(refreshBtn);
		add(buttonPanel, BorderLayout.NORTH);
	}

	private void onAdd(ActionEvent e) {
		CardLevelDialog dialog = new CardLevelDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getCardLevelDAO().addCardLevel(dialog.getCardLevel());
			loadData();
		}
	}

	private void onEdit(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите запись для редактирования.");
			return;
		}
		Long id = (Long) tableModel.getValueAt(selectedRow, 0);
		String name = (String) tableModel.getValueAt(selectedRow, 1);
		java.math.BigDecimal discount = (java.math.BigDecimal) tableModel.getValueAt(selectedRow, 2);
		java.math.BigDecimal multiplier = (java.math.BigDecimal) tableModel.getValueAt(selectedRow, 3);
		CardLevel level = new CardLevel(id, name, discount, multiplier);

		CardLevelDialog dialog = new CardLevelDialog((Frame) SwingUtilities.getWindowAncestor(this), level);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getCardLevelDAO().updateCardLevel(dialog.getCardLevel());
			loadData();
		}
	}

	private void onDelete(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите запись для удаления.");
			return;
		}
		Long id = (Long) tableModel.getValueAt(selectedRow, 0);
		int confirm = JOptionPane.showConfirmDialog(this, "Удалить уровень ID=" + id + "?", "Подтверждение",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				factory.getCardLevelDAO().deleteCardLevel(id);
				loadData();
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
			}
		}
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<CardLevel> levels = factory.getCardLevelDAO().getAllCardLevels();
		for (CardLevel l : levels) {
			tableModel.addRow(new Object[] { l.id(), l.name(), l.discountPercent(), l.bonusMultiplier() });
		}
	}
}