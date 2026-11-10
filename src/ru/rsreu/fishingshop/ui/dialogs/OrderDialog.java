package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Customer;
import ru.rsreu.fishingshop.model.Seller;
import ru.rsreu.fishingshop.model.Order;
import ru.rsreu.fishingshop.model.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDialog extends JDialog {
	private JComboBox<Customer> customerCombo;
	private JComboBox<Seller> sellerCombo;
	private JTextArea itemsArea;
	private boolean confirmed = false;
	private Order order;
	private List<OrderItem> items;
	private final DAOFactory factory;

	public OrderDialog(Frame parent, DAOFactory factory) {
		super(parent, "Создать заказ", true);
		this.factory = factory;
		setSize(500, 400);
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
		add(new JLabel("Покупатель:"), gbc);
		gbc.gridx = 1;
		customerCombo = new JComboBox<>();
		loadCustomers();
		add(customerCombo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Продавец:"), gbc);
		gbc.gridx = 1;
		sellerCombo = new JComboBox<>();
		loadSellers();
		add(sellerCombo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Позиции (productId:quantity):"), gbc);
		gbc.gridx = 1;
		itemsArea = new JTextArea(5, 20);
		JScrollPane scroll = new JScrollPane(itemsArea);
		add(scroll, gbc);

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

	private void loadSellers() {
		List<Seller> sellers = factory.getSellerDAO().getAllSellers();
		for (Seller s : sellers) {
			sellerCombo.addItem(s);
		}
		sellerCombo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value instanceof Seller) {
					setText(((Seller) value).fullName() + " (ID=" + ((Seller) value).id() + ")");
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
	}

	private void onOk() {
		Customer customer = (Customer) customerCombo.getSelectedItem();
		Seller seller = (Seller) sellerCombo.getSelectedItem();
		if (customer == null || seller == null) {
			JOptionPane.showMessageDialog(this, "Выберите покупателя и продавца.");
			return;
		}
		String itemsText = itemsArea.getText().trim();
		if (itemsText.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Добавьте хотя бы одну позицию.");
			return;
		}
		items = new ArrayList<>();
		String[] lines = itemsText.split("\n");
		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty())
				continue;
			String[] parts = line.split(":");
			if (parts.length != 2) {
				JOptionPane.showMessageDialog(this, "Неверный формат позиции: " + line);
				return;
			}
			try {
				Long productId = Long.parseLong(parts[0].trim());
				int quantity = Integer.parseInt(parts[1].trim());
				if (quantity <= 0) {
					JOptionPane.showMessageDialog(this, "Количество должно быть > 0: " + line);
					return;
				}
				items.add(new OrderItem(null, null, productId, quantity, BigDecimal.ZERO));
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Ошибка в строке: " + line);
				return;
			}
		}
		if (items.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Нет корректных позиций.");
			return;
		}
		
		order = new Order(null, LocalDateTime.now(), "Оформлен", BigDecimal.ZERO, customer.id(), seller.id());
		confirmed = true;
		dispose();
	}

	private void onCancel() {
		confirmed = false;
		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public Order getOrder() {
		return order;
	}

	public List<OrderItem> getItems() {
		return items;
	}
}