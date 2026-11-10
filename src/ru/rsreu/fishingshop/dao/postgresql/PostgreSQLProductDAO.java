package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.ProductDAO;
import ru.rsreu.fishingshop.model.Product;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLProductDAO implements ProductDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLProductDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addProduct(Product product) {
		String sql = RESOURCER.getString("procedure.add_product");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setString(1, product.name());
			stmt.setString(2, product.description());
			stmt.setBigDecimal(3, product.price());
			stmt.setInt(4, product.stockQuantity());
			stmt.setLong(5, product.categoryId());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при добавлении товара: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateProduct(Product product) {
		String sql = RESOURCER.getString("procedure.update_product");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, product.id());
			stmt.setString(2, product.name());
			stmt.setString(3, product.description());
			stmt.setBigDecimal(4, product.price());
			stmt.setInt(5, product.stockQuantity());
			stmt.setLong(6, product.categoryId());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении товара: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Product> getAllProducts() {
		List<Product> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.products");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(
						new Product(rs.getLong("product_id"), rs.getString("product_name"), rs.getString("description"),
								rs.getBigDecimal("price"), rs.getInt("stock_quantity"), rs.getLong("category_id")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка товаров: " + e.getMessage(), e);
		}
		return list;
	}
}