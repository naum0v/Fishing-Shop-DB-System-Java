package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Seller;
import ru.rsreu.fishingshop.ui.dialogs.SellerDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class SellerPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public SellerPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "ФИО", "Телефон", "Дата найма" };
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
		SellerDialog dialog = new SellerDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getSellerDAO().addSeller(dialog.getSeller());
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
		String hireDateStr = (String) tableModel.getValueAt(selectedRow, 3);
		Seller seller = new Seller(id, fullName, phone, LocalDate.parse(hireDateStr));

		SellerDialog dialog = new SellerDialog((Frame) SwingUtilities.getWindowAncestor(this), seller);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getSellerDAO().updateSeller(dialog.getSeller());
			loadData();
		}
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<Seller> sellers = factory.getSellerDAO().getAllSellers();
		for (Seller s : sellers) {
			tableModel.addRow(new Object[] { s.id(), s.fullName(), s.phone(), s.hireDate().toString() });
		}
	}
}