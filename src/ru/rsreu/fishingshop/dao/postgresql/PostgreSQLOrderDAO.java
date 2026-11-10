package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.OrderDAO;
import ru.rsreu.fishingshop.model.Order;
import ru.rsreu.fishingshop.model.OrderItem;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLOrderDAO implements OrderDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLOrderDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Long createOrder(Order order, List<OrderItem> items) {
		String sql = RESOURCER.getString("procedure.create_order");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, order.customerId());
			stmt.setLong(2, order.sellerId());
			stmt.setTimestamp(3, Timestamp.valueOf(order.orderDateTime()));
			stmt.setString(4, order.status());
			stmt.setBigDecimal(5, order.totalAmount());

			StringBuilder jsonBuilder = new StringBuilder("[");
			for (int i = 0; i < items.size(); i++) {
				OrderItem item = items.get(i);
				if (i > 0)
					jsonBuilder.append(",");
				jsonBuilder.append("{").append("\"product_id\":").append(item.productId()).append(",")
						.append("\"quantity\":").append(item.quantity()).append("}");
			}
			jsonBuilder.append("]");
			String json = jsonBuilder.toString();
			stmt.setObject(6, json, java.sql.Types.OTHER);

			stmt.execute();

			String sqlCurrval = RESOURCER.getString("query.currval.order");
			try (Statement stmt2 = connection.createStatement(); ResultSet rs = stmt2.executeQuery(sqlCurrval)) {
				if (rs.next()) {
					return rs.getLong(1);
				} else {
					throw new RuntimeException("Не удалось получить ID созданного заказа");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при создании заказа: " + e.getMessage(), e);
		}
	}

	@Override
	public void addOrderItem(Long orderId, Long productId, Integer quantity) {
		String sql = RESOURCER.getString("procedure.add_order_item");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, orderId);
			stmt.setLong(2, productId);
			stmt.setInt(3, quantity);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при добавлении позиции в заказ: " + e.getMessage(), e);
		}
	}

	@Override
	public void removeOrderItem(Long orderId, Long productId) {
		String sql = RESOURCER.getString("procedure.remove_order_item");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, orderId);
			stmt.setLong(2, productId);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при удалении позиции из заказа: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateOrderStatus(Long orderId, String newStatus) {
		String sql = RESOURCER.getString("procedure.update_order_status");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, orderId);
			stmt.setString(2, newStatus);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при изменении статуса заказа: " + e.getMessage(), e);
		}
	}

	@Override
	public void cancelOrder(Long orderId) {
		String sql = RESOURCER.getString("procedure.cancel_order");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, orderId);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при отмене заказа: " + e.getMessage(), e);
		}
	}

	@Override
	public void applyDiscount(Long orderId, BigDecimal discountPercent) {
		String sql = RESOURCER.getString("procedure.apply_discount");
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.setLong(1, orderId);
			stmt.setBigDecimal(2, discountPercent);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при применении скидки: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Order> getAllOrders() {
		List<Order> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.select.all.orders");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Order(rs.getLong("order_id"), rs.getTimestamp("order_date_time").toLocalDateTime(),
						rs.getString("order_status"), rs.getBigDecimal("total_amount"), rs.getLong("customer_id"),
						rs.getLong("seller_id")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении списка заказов: " + e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<OrderItem> getOrderItems(Long orderId) {
		List<OrderItem> list = new ArrayList<>();
		String sql = "SELECT * FROM orderitems WHERE order_id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, orderId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(new OrderItem(rs.getLong("order_item_id"), rs.getLong("order_id"),
							rs.getLong("product_id"), rs.getInt("quantity"), rs.getBigDecimal("unit_price")));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении позиций заказа: " + e.getMessage(), e);
		}
		return list;
	}
}