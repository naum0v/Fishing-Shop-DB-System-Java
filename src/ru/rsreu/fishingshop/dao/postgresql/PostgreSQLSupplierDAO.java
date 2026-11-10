package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.SupplierDAO;
import ru.rsreu.fishingshop.model.Supplier;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLSupplierDAO implements SupplierDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLSupplierDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addSupplier(Supplier supplier) {
		String sql = RESOURCER.getString("procedure.add_supplier");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setString(1, supplier.companyName());
			stmt.setString(2, supplier.contactPerson());
			stmt.setString(3, supplier.phone());
			stmt.setString(4, supplier.address());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при добавлении поставщика: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateSupplier(Supplier supplier) {
		String sql = RESOURCER.getString("procedure.update_supplier");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, supplier.id());
			stmt.setString(2, supplier.companyName());
			stmt.setString(3, supplier.contactPerson());
			stmt.setString(4, supplier.phone());
			stmt.setString(5, supplier.address());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении поставщика: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteSupplier(Long id) {
		String sql = RESOURCER.getString("procedure.delete_supplier");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, id);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при удалении поставщика: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Supplier> getAllSuppliers() {
		List<Supplier> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.suppliers");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Supplier(rs.getLong("supplier_id"), rs.getString("company_name"),
						rs.getString("contact_person"), rs.getString("phone"), rs.getString("address")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка поставщиков: " + e.getMessage(), e);
		}
		return list;
	}
}