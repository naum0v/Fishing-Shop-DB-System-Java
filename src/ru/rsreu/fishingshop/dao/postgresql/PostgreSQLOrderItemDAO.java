package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.OrderItemDAO;
import java.sql.Connection;

public class PostgreSQLOrderItemDAO implements OrderItemDAO {
	private final Connection connection;

	public PostgreSQLOrderItemDAO(Connection connection) {
		this.connection = connection;
	}
}