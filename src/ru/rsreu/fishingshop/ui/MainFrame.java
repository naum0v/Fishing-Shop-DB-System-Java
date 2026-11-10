package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.dao.DBType;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
	private final DAOFactory factory;

	public MainFrame() {
		this.factory = DAOFactory.getInstance(DBType.POSTGRESQL);
		setTitle("Рыболовный магазин");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 750);
		setLocationRelativeTo(null);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Покупатели", new CustomerPanel(factory));
		tabbedPane.addTab("Товары", new ProductPanel(factory));
		tabbedPane.addTab("Категории", new CategoryPanel(factory));
		tabbedPane.addTab("Поставщики", new SupplierPanel(factory));
		tabbedPane.addTab("Продавцы", new SellerPanel(factory));
		tabbedPane.addTab("Карты лояльности", new LoyaltyCardPanel(factory));
		tabbedPane.addTab("Уровни карт", new CardLevelPanel(factory));
		tabbedPane.addTab("Заказы", new OrderPanel(factory));
		tabbedPane.addTab("Отчёты", new ReportPanel(factory));

		add(tabbedPane, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new MainFrame().setVisible(true);
		});
	}
}