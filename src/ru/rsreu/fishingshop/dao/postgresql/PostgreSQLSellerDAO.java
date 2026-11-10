package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.SellerDAO;
import ru.rsreu.fishingshop.model.Seller;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLSellerDAO implements SellerDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLSellerDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addSeller(Seller seller) {
		String sql = RESOURCER.getString("procedure.add_seller");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setString(1, seller.fullName());
			stmt.setString(2, seller.phone());
			stmt.setDate(3, Date.valueOf(seller.hireDate()));
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при добавлении продавца: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateSeller(Seller seller) {
		String sql = RESOURCER.getString("procedure.update_seller");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, seller.id());
			stmt.setString(2, seller.fullName());
			stmt.setString(3, seller.phone());
			stmt.setDate(4, Date.valueOf(seller.hireDate()));
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении продавца: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Seller> getAllSellers() {
		List<Seller> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.sellers");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Seller(rs.getLong("seller_id"), rs.getString("full_name"), rs.getString("phone"),
						rs.getDate("hire_date").toLocalDate()));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка продавцов: " + e.getMessage(), e);
		}
		return list;
	}
}