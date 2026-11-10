package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.CustomerDAO;
import ru.rsreu.fishingshop.model.Customer;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLCustomerDAO implements CustomerDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLCustomerDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addCustomer(Customer customer) {
		String sql = RESOURCER.getString("procedure.add_customer");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setString(1, customer.fullName());
			stmt.setString(2, customer.phone());
			stmt.setString(3, customer.email());
			stmt.setString(4, customer.address());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при добавлении покупателя: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateCustomer(Customer customer) {
		String sql = RESOURCER.getString("procedure.update_customer");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, customer.id());
			stmt.setString(2, customer.fullName());
			stmt.setString(3, customer.phone());
			stmt.setString(4, customer.email());
			stmt.setString(5, customer.address());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении покупателя: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteCustomer(Long id) {
		String sql = RESOURCER.getString("procedure.delete_customer");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, id);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при удалении покупателя: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Customer> getAllCustomers() {
		List<Customer> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.customers");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Customer(rs.getLong("customer_id"), rs.getString("full_name"), rs.getString("phone"),
						rs.getString("email"), rs.getString("address")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка покупателей: " + e.getMessage(), e);
		}
		return list;
	}
}