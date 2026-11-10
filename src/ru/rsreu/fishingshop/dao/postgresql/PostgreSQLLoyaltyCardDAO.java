package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.LoyaltyCardDAO;
import ru.rsreu.fishingshop.model.LoyaltyCard;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLLoyaltyCardDAO implements LoyaltyCardDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLLoyaltyCardDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addLoyaltyCard(LoyaltyCard card) {
		String sql = RESOURCER.getString("procedure.add_loyalty_card");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setString(1, card.cardNumber());
			stmt.setDate(2, Date.valueOf(card.registrationDate()));
			stmt.setBigDecimal(3, card.accumulatedPoints());
			stmt.setLong(4, card.customerId());
			stmt.setLong(5, card.levelId());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при регистрации карты: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateLoyaltyCard(Long cardId, Long levelId) {
		String sql = RESOURCER.getString("procedure.update_loyalty_card");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, cardId);
			stmt.setLong(2, levelId);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении карты: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteLoyaltyCard(Long cardId) {
		String sql = RESOURCER.getString("procedure.delete_loyalty_card");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, cardId);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при удалении карты: " + e.getMessage(), e);
		}
	}

	@Override
	public void applyBonusPoints(Long customerId, BigDecimal pointsToSpend) {
		String sql = RESOURCER.getString("procedure.apply_bonus_points");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, customerId);
			stmt.setBigDecimal(2, pointsToSpend);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при списании баллов: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateCardLevelBySpending(Long customerId) {
		String sql = RESOURCER.getString("procedure.update_card_level");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, customerId);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при обновлении уровня карты: " + e.getMessage(), e);
		}
	}

	@Override
	public List<LoyaltyCard> getAllLoyaltyCards() {
		List<LoyaltyCard> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.loyalty_cards");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new LoyaltyCard(rs.getLong("loyalty_card_id"), rs.getString("card_number"),
						rs.getDate("registration_date").toLocalDate(), rs.getBigDecimal("accumulated_points"),
						rs.getLong("customer_id"), rs.getLong("level_id")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка карт: " + e.getMessage(), e);
		}
		return list;
	}
}