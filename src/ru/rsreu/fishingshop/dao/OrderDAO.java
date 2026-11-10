package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.Order;
import ru.rsreu.fishingshop.model.OrderItem;
import java.math.BigDecimal;
import java.util.List;

public interface OrderDAO {
	Long createOrder(Order order, List<OrderItem> items);

	void addOrderItem(Long orderId, Long productId, Integer quantity);

	void removeOrderItem(Long orderId, Long productId);

	void updateOrderStatus(Long orderId, String newStatus);

	void cancelOrder(Long orderId);

	void applyDiscount(Long orderId, BigDecimal discountPercent);

	List<Order> getAllOrders();

	List<OrderItem> getOrderItems(Long orderId);
}