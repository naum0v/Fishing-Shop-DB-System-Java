package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Category;
import ru.rsreu.fishingshop.ui.dialogs.CategoryDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CategoryPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public CategoryPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "Название", "Описание" };
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
		JButton refreshBtn = new JButton("Обновить");

		addBtn.addActionListener(this::onAdd);
		editBtn.addActionListener(this::onEdit);
		refreshBtn.addActionListener(e -> loadData());

		buttonPanel.add(addBtn);
		buttonPanel.add(editBtn);
		buttonPanel.add(refreshBtn);
		add(buttonPanel, BorderLayout.NORTH);
	}

	private void onAdd(ActionEvent e) {
		CategoryDialog dialog = new CategoryDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getCategoryDAO().addCategory(dialog.getCategory());
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
		String description = (String) tableModel.getValueAt(selectedRow, 2);
		Category category = new Category(id, name, description);

		CategoryDialog dialog = new CategoryDialog((Frame) SwingUtilities.getWindowAncestor(this), category);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getCategoryDAO().updateCategory(dialog.getCategory());
			loadData();
		}
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<Category> categories = factory.getCategoryDAO().getAllCategories();
		for (Category cat : categories) {
			tableModel.addRow(new Object[] { cat.id(), cat.name(), cat.description() });
		}
	}
}