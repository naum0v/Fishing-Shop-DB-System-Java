package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQLDAOFactory extends DAOFactory {
	private static volatile PostgreSQLDAOFactory instance;
	private Connection connection;

	private PostgreSQLDAOFactory() {
		try {
			Class.forName("org.postgresql.Driver");
			Properties props = loadProperties();
			String url = props.getProperty("db.url");
			String user = props.getProperty("db.user");
			String password = props.getProperty("db.password");
			this.connection = DriverManager.getConnection(url, user, password);
			System.out.println(props.getProperty("connection.success"));
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException(String.format("Failed to connect: %s", e.getMessage()), e);
		}
	}

	private Properties loadProperties() {
		Properties props = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("text.properties")) {
			if (input == null) {
				throw new RuntimeException("Resource text.properties not found");
			}
			props.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties", e);
		}
		return props;
	}

	public static PostgreSQLDAOFactory getInstance() {
		if (instance == null) {
			synchronized (PostgreSQLDAOFactory.class) {
				if (instance == null) {
					instance = new PostgreSQLDAOFactory();
				}
			}
		}
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public CustomerDAO getCustomerDAO() {
		return new PostgreSQLCustomerDAO(connection);
	}

	@Override
	public ProductDAO getProductDAO() {
		return new PostgreSQLProductDAO(connection);
	}

	@Override
	public OrderDAO getOrderDAO() {
		return new PostgreSQLOrderDAO(connection);
	}

	@Override
	public OrderItemDAO getOrderItemDAO() {
		return new PostgreSQLOrderItemDAO(connection);
	}

	@Override
	public SupplierDAO getSupplierDAO() {
		return new PostgreSQLSupplierDAO(connection);
	}

	@Override
	public CategoryDAO getCategoryDAO() {
		return new PostgreSQLCategoryDAO(connection);
	}

	@Override
	public LoyaltyCardDAO getLoyaltyCardDAO() {
		return new PostgreSQLLoyaltyCardDAO(connection);
	}

	@Override
	public CardLevelDAO getCardLevelDAO() {
		return new PostgreSQLCardLevelDAO(connection);
	}

	@Override
	public SellerDAO getSellerDAO() {
		return new PostgreSQLSellerDAO(connection);
	}

	@Override
	public ProductSupplyDAO getProductSupplyDAO() {
		return new PostgreSQLProductSupplyDAO(connection);
	}

	@Override
	public ReportDAO getReportDAO() {
		return new PostgreSQLReportDAO(connection);
	}
}