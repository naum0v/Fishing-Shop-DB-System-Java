package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.CardLevelDAO;
import ru.rsreu.fishingshop.model.CardLevel;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLCardLevelDAO implements CardLevelDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLCardLevelDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addCardLevel(CardLevel level) {
		String sql = RESOURCER.getString("procedure.add_card_level");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setString(1, level.name());
			stmt.setBigDecimal(2, level.discountPercent());
			stmt.setBigDecimal(3, level.bonusMultiplier());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при добавлении уровня карты: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateCardLevel(CardLevel level) {
		String sql = RESOURCER.getString("procedure.update_card_level");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, level.id());
			stmt.setString(2, level.name());
			stmt.setBigDecimal(3, level.discountPercent());
			stmt.setBigDecimal(4, level.bonusMultiplier());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении уровня карты: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteCardLevel(Long id) {
		String sql = RESOURCER.getString("procedure.delete_card_level");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, id);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при удалении уровня карты: " + e.getMessage(), e);
		}
	}

	@Override
	public List<CardLevel> getAllCardLevels() {
		List<CardLevel> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.card_levels");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new CardLevel(rs.getLong("level_id"), rs.getString("level_name"),
						rs.getBigDecimal("discount_percent"), rs.getBigDecimal("bonus_multiplier")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка уровней карт: " + e.getMessage(), e);
		}
		return list;
	}
}