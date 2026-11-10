package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Customer;
import ru.rsreu.fishingshop.ui.dialogs.CustomerDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CustomerPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public CustomerPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "ФИО", "Телефон", "Email", "Адрес" };
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
		CustomerDialog dialog = new CustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getCustomerDAO().addCustomer(dialog.getCustomer());
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
		String fullName = (String) tableModel.getValueAt(selectedRow, 1);
		String phone = (String) tableModel.getValueAt(selectedRow, 2);
		String email = (String) tableModel.getValueAt(selectedRow, 3);
		String address = (String) tableModel.getValueAt(selectedRow, 4);
		Customer customer = new Customer(id, fullName, phone, email, address);

		CustomerDialog dialog = new CustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), customer);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getCustomerDAO().updateCustomer(dialog.getCustomer());
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
		int confirm = JOptionPane.showConfirmDialog(this, "Удалить покупателя ID=" + id + "?", "Подтверждение",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				factory.getCustomerDAO().deleteCustomer(id);
				loadData();
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
			}
		}
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<Customer> customers = factory.getCustomerDAO().getAllCustomers();
		for (Customer c : customers) {
			tableModel.addRow(new Object[] { c.id(), c.fullName(), c.phone(), c.email(), c.address() });
		}
	}
}