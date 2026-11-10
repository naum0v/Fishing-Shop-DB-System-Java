package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Supplier;
import ru.rsreu.fishingshop.ui.dialogs.SupplierDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SupplierPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public SupplierPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "Компания", "Контактное лицо", "Телефон", "Адрес" };
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
		SupplierDialog dialog = new SupplierDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getSupplierDAO().addSupplier(dialog.getSupplier());
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
		String company = (String) tableModel.getValueAt(selectedRow, 1);
		String contact = (String) tableModel.getValueAt(selectedRow, 2);
		String phone = (String) tableModel.getValueAt(selectedRow, 3);
		String address = (String) tableModel.getValueAt(selectedRow, 4);
		Supplier supplier = new Supplier(id, company, contact, phone, address);

		SupplierDialog dialog = new SupplierDialog((Frame) SwingUtilities.getWindowAncestor(this), supplier);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getSupplierDAO().updateSupplier(dialog.getSupplier());
			loadData();
		}
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<Supplier> suppliers = factory.getSupplierDAO().getAllSuppliers();
		for (Supplier s : suppliers) {
			tableModel.addRow(new Object[] { s.id(), s.companyName(), s.contactPerson(), s.phone(), s.address() });
		}
	}
}