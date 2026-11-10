package ru.rsreu.fishingshop.ui.dialogs;

import ru.rsreu.fishingshop.model.Category;

import javax.swing.*;
import java.awt.*;

public class CategoryDialog extends JDialog {
	private JTextField nameField, descriptionField;
	private boolean confirmed = false;
	private Category category;

	public CategoryDialog(Frame parent, Category existing) {
		super(parent, existing == null ? "Добавить категорию" : "Изменить категорию", true);
		this.category = existing;
		setSize(400, 200);
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

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton okBtn = new JButton("OK");
		JButton cancelBtn = new JButton("Отмена");
		okBtn.addActionListener(e -> onOk());
		cancelBtn.addActionListener(e -> onCancel());
		buttonPanel.add(okBtn);
		buttonPanel.add(cancelBtn);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		add(buttonPanel, gbc);

		if (category != null) {
			nameField.setText(category.name());
			descriptionField.setText(category.description());
		}
	}

	private void onOk() {
		String name = nameField.getText().trim();
		String description = descriptionField.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Название обязательно.");
			return;
		}
		if (category != null) {
			category = new Category(category.id(), name, description);
		} else {
			category = new Category(null, name, description);
		}
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

	public Category getCategory() {
		return category;
	}
}