package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.ProductSupplyDAO;
import ru.rsreu.fishingshop.model.ProductSupply;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLProductSupplyDAO implements ProductSupplyDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLProductSupplyDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void receiveSupply(ProductSupply supply, Integer quantity) {
		String sql = RESOURCER.getString("procedure.receive_supply");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, supply.supplierId());
			stmt.setLong(2, supply.productId());
			stmt.setDate(3, Date.valueOf(supply.lastSupplyDate()));
			stmt.setBigDecimal(4, supply.purchasePrice());
			stmt.setInt(5, quantity);
			stmt.setBoolean(6, supply.isPrimarySupplier());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при приёмке поставки: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateSupplyPrice(Long supplyId, BigDecimal newPrice) {
		String sql = RESOURCER.getString("procedure.update_supply_price");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, supplyId);
			stmt.setBigDecimal(2, newPrice);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении цены поставки: " + e.getMessage(), e);
		}
	}

	@Override
	public void removeSupply(Long supplyId) {
		String sql = RESOURCER.getString("procedure.remove_supply");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, supplyId);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при удалении поставки: " + e.getMessage(), e);
		}
	}

	@Override
	public List<ProductSupply> getAllSupplies() {
		List<ProductSupply> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.product_supplies");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new ProductSupply(rs.getLong("product_supply_id"), rs.getLong("supplier_id"),
						rs.getLong("product_id"), rs.getDate("last_supply_date").toLocalDate(),
						rs.getBigDecimal("purchase_price"), rs.getBoolean("is_primary_supplier")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка поставок: " + e.getMessage(), e);
		}
		return list;
	}
}