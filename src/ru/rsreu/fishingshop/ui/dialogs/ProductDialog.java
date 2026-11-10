package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.Category;
import ru.rsreu.fishingshop.model.Product;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductDialog extends JDialog {
	private JTextField nameField, descriptionField, priceField, stockField;
	private JComboBox<Category> categoryCombo;
	private boolean confirmed = false;
	private Product product;
	private final DAOFactory factory;

	public ProductDialog(Frame parent, Product existing, DAOFactory factory) {
		super(parent, existing == null ? "Добавить товар" : "Изменить товар", true);
		this.factory = factory;
		this.product = existing;
		setSize(800, 350);
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
		add(new JLabel("Название:"), gbc);
		gbc.gridx = 1;
		nameField = new JTextField(20);
		add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Описание:"), gbc);
		gbc.gridx = 1;
		descriptionField = new JTextField(20);
		add(descriptionField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Цена:"), gbc);
		gbc.gridx = 1;
		priceField = new JTextField(20);
		add(priceField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		add(new JLabel("Остаток:"), gbc);
		gbc.gridx = 1;
		stockField = new JTextField(20);
		add(stockField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		add(new JLabel("Категория:"), gbc);
		gbc.gridx = 1;
		categoryCombo = new JComboBox<>();
		loadCategories();
		add(categoryCombo, gbc);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton okBtn = new JButton("OK");
		JButton cancelBtn = new JButton("Отмена");
		okBtn.addActionListener(e -> onOk());
		cancelBtn.addActionListener(e -> onCancel());
		buttonPanel.add(okBtn);
		buttonPanel.add(cancelBtn);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		add(buttonPanel, gbc);

		if (product != null) {
			nameField.setText(product.name());
			descriptionField.setText(product.description());
			priceField.setText(product.price().toString());
			stockField.setText(product.stockQuantity().toString());
			
			for (int i = 0; i < categoryCombo.getItemCount(); i++) {
				Category cat = categoryCombo.getItemAt(i);
				if (cat.id().equals(product.categoryId())) {
					categoryCombo.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private void loadCategories() {
		List<Category> categories = factory.getCategoryDAO().getAllCategories();
		for (Category cat : categories) {
			categoryCombo.addItem(cat);
		}
		categoryCombo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value instanceof Category) {
					setText(((Category) value).name());
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
	}

	private void onOk() {
		try {
			String name = nameField.getText().trim();
			String description = descriptionField.getText().trim();
			BigDecimal price = new BigDecimal(priceField.getText().trim());
			Integer stock = Integer.parseInt(stockField.getText().trim());
			Category selectedCategory = (Category) categoryCombo.getSelectedItem();
			if (selectedCategory == null) {
				JOptionPane.showMessageDialog(this, "Выберите категорию.");
				return;
			}
			if (name.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Название обязательно.");
				return;
			}
			if (price.compareTo(BigDecimal.ZERO) <= 0) {
				JOptionPane.showMessageDialog(this, "Цена должна быть положительной.");
				return;
			}
			if (stock < 0) {
				JOptionPane.showMessageDialog(this, "Остаток не может быть отрицательным.");
				return;
			}
			if (product != null) {
				product = new Product(product.id(), name, description, price, stock, selectedCategory.id());
			} else {
				product = new Product(null, name, description, price, stock, selectedCategory.id());
			}
			confirmed = true;
			dispose();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Введите корректные числовые значения.");
		}
	}

	private void onCancel() {
		confirmed = false;
		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public Product getProduct() {
		return product;
	}
}