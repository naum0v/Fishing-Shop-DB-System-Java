package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.CategoryDAO;
import ru.rsreu.fishingshop.model.Category;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLCategoryDAO implements CategoryDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLCategoryDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addCategory(Category category) {
		String sql = RESOURCER.getString("procedure.add_category");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setString(1, category.name());
			stmt.setString(2, category.description());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при добавлении категории: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateCategory(Category category) {
		String sql = RESOURCER.getString("procedure.update_category");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, category.id());
			stmt.setString(2, category.name());
			stmt.setString(3, category.description());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении категории: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteCategory(Long id) {
		String sql = RESOURCER.getString("procedure.delete_category");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, id);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при удалении категории: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Category> getAllCategories() {
		List<Category> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.categories");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Category(rs.getLong("category_id"), rs.getString("category_name"),
						rs.getString("description")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка категорий: " + e.getMessage(), e);
		}
		return list;
	}
}