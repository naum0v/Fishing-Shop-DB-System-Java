package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Product;
import ru.rsreu.fishingshop.ui.dialogs.ProductDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ProductPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public ProductPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "Название", "Описание", "Цена", "Остаток", "Категория" };
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
		ProductDialog dialog = new ProductDialog((Frame) SwingUtilities.getWindowAncestor(this), null, factory);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getProductDAO().addProduct(dialog.getProduct());
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
		java.math.BigDecimal price = (java.math.BigDecimal) tableModel.getValueAt(selectedRow, 3);
		Integer stock = (Integer) tableModel.getValueAt(selectedRow, 4);
		Long categoryId = (Long) tableModel.getValueAt(selectedRow, 5);
		Product product = new Product(id, name, description, price, stock, categoryId);

		ProductDialog dialog = new ProductDialog((Frame) SwingUtilities.getWindowAncestor(this), product, factory);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getProductDAO().updateProduct(dialog.getProduct());
			loadData();
		}
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<Product> products = factory.getProductDAO().getAllProducts();
		for (Product p : products) {
			tableModel.addRow(
					new Object[] { p.id(), p.name(), p.description(), p.price(), p.stockQuantity(), p.categoryId() });
		}
	}
}