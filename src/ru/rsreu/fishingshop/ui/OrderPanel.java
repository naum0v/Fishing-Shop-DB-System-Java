package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Order;
import ru.rsreu.fishingshop.model.OrderItem;
import ru.rsreu.fishingshop.ui.dialogs.OrderDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class OrderPanel extends JPanel {
	private final DAOFactory factory;
	private JTable table;
	private DefaultTableModel tableModel;

	public OrderPanel(DAOFactory factory) {
		this.factory = factory;
		setLayout(new BorderLayout(10, 10));
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		String[] columns = { "ID", "Дата/время", "Статус", "Сумма", "Покупатель ID", "Продавец ID" };
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
		JButton createBtn = new JButton("Создать заказ");
		JButton addItemBtn = new JButton("Добавить позицию");
		JButton statusBtn = new JButton("Изменить статус");
		JButton cancelBtn = new JButton("Отменить заказ");
		JButton showItemsBtn = new JButton("Показать позиции");
		JButton refreshBtn = new JButton("Обновить");

		createBtn.addActionListener(e -> onCreateOrder());
		addItemBtn.addActionListener(e -> onAddItem());
		statusBtn.addActionListener(e -> onChangeStatus());
		cancelBtn.addActionListener(e -> onCancelOrder());
		showItemsBtn.addActionListener(e -> onShowItems());
		refreshBtn.addActionListener(e -> loadData());

		buttonPanel.add(createBtn);
		buttonPanel.add(addItemBtn);
		buttonPanel.add(statusBtn);
		buttonPanel.add(cancelBtn);
		buttonPanel.add(showItemsBtn);
		buttonPanel.add(refreshBtn);
		add(buttonPanel, BorderLayout.NORTH);
	}

	private void onCreateOrder() {
		OrderDialog dialog = new OrderDialog((Frame) SwingUtilities.getWindowAncestor(this), factory);
		dialog.setVisible(true);
		if (dialog.isConfirmed()) {
			factory.getOrderDAO().createOrder(dialog.getOrder(), dialog.getItems());
			loadData();
		}
	}

	private void onAddItem() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите заказ.");
			return;
		}
		Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
		String productIdStr = JOptionPane.showInputDialog(this, "Введите ID товара:");
		if (productIdStr == null)
			return;
		String quantityStr = JOptionPane.showInputDialog(this, "Введите количество:");
		if (quantityStr == null)
			return;
		try {
			Long productId = Long.parseLong(productIdStr);
			int quantity = Integer.parseInt(quantityStr);
			factory.getOrderDAO().addOrderItem(orderId, productId, quantity);
			loadData();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Введите корректные числа.");
		} catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
		}
	}

	private void onChangeStatus() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите заказ.");
			return;
		}
		Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
		String[] statuses = { "Оформлен", "Оплачен", "Выполнен", "Отменён" };
		String newStatus = (String) JOptionPane.showInputDialog(this, "Выберите новый статус", "Изменение статуса",
				JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
		if (newStatus != null) {
			try {
				factory.getOrderDAO().updateOrderStatus(orderId, newStatus);
				loadData();
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
			}
		}
	}

	private void onCancelOrder() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите заказ.");
			return;
		}
		Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
		int confirm = JOptionPane.showConfirmDialog(this, "Отменить заказ ID=" + orderId + "?", "Подтверждение",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				factory.getOrderDAO().cancelOrder(orderId);
				loadData();
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
			}
		}
	}

	private void onShowItems() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Выберите заказ.");
			return;
		}
		Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
		List<OrderItem> items = factory.getOrderDAO().getOrderItems(orderId);
		if (items.isEmpty()) {
			JOptionPane.showMessageDialog(this, "В заказе нет позиций.");
			return;
		}
		
		JDialog itemsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Позиции заказа #" + orderId,
				true);
		itemsDialog.setSize(500, 300);
		itemsDialog.setLocationRelativeTo(this);
		String[] cols = { "ID позиции", "Товар ID", "Количество", "Цена за единицу" };
		DefaultTableModel model = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		for (OrderItem item : items) {
			model.addRow(new Object[] { item.id(), item.productId(), item.quantity(), item.unitPrice() });
		}
		JTable itemTable = new JTable(model);
		itemsDialog.add(new JScrollPane(itemTable), BorderLayout.CENTER);
		itemsDialog.setVisible(true);
	}

	private void loadData() {
		tableModel.setRowCount(0);
		List<Order> orders = factory.getOrderDAO().getAllOrders();
		for (Order o : orders) {
			tableModel.addRow(new Object[] { o.id(), o.orderDateTime(), o.status(), o.totalAmount(), o.customerId(),
					o.sellerId() });
		}
	}
}