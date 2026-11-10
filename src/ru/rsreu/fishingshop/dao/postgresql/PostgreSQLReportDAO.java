package ru.rsreu.fishingshop.dao.postgresql;

import ru.rsreu.fishingshop.dao.ReportDAO;
import ru.rsreu.fishingshop.model.SalesReportRow;
import ru.rsreu.fishingshop.model.SupplierEfficiencyRow;
import ru.rsreu.fishingshop.model.CustomerBonusRow;
import ru.rsreu.fishingshop.utils.Resourcer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLReportDAO implements ReportDAO {
	private final Connection connection;
	private static final Resourcer RESOURCER = Resourcer.getInstance();

	public PostgreSQLReportDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<SalesReportRow> getSalesReport(LocalDate dateFrom, LocalDate dateTo) {
		List<SalesReportRow> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.sales.report");
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(dateFrom));
			stmt.setDate(2, Date.valueOf(dateTo));
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(new SalesReportRow(rs.getDate("order_date").toLocalDate(), rs.getLong("order_count"),
							rs.getBigDecimal("total_sales"), rs.getBigDecimal("avg_check")));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении отчёта по продажам: " + e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<SupplierEfficiencyRow> getSupplierEfficiency(LocalDate dateFrom, LocalDate dateTo) {
		List<SupplierEfficiencyRow> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.supplier.efficiency");
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(dateFrom));
			stmt.setDate(2, Date.valueOf(dateTo));
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(new SupplierEfficiencyRow(rs.getString("supplier_name"), rs.getLong("total_supplies"),
							rs.getBigDecimal("total_purchase"), rs.getBigDecimal("avg_price")));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении отчёта по поставщикам: " + e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<CustomerBonusRow> getCustomerBonusSummary(Long customerId) {
		List<CustomerBonusRow> list = new ArrayList<>();
		String sql = RESOURCER.getString("query.customer.bonus");
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, customerId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(new CustomerBonusRow(rs.getString("customer_name"), rs.getString("card_number"),
							rs.getString("level_name"), rs.getBigDecimal("accumulated_points"),
							rs.getBigDecimal("total_purchases"), rs.getLong("total_orders")));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Ошибка при получении сводки по покупателю: " + e.getMessage(), e);
		}
		return list;
	}
}