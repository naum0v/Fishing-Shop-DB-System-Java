package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.SalesReportRow;
import ru.rsreu.fishingshop.model.SupplierEfficiencyRow;
import ru.rsreu.fishingshop.model.CustomerBonusRow;

import java.time.LocalDate;
import java.util.List;

public interface ReportDAO {
	List<SalesReportRow> getSalesReport(LocalDate dateFrom, LocalDate dateTo);

	List<SupplierEfficiencyRow> getSupplierEfficiency(LocalDate dateFrom, LocalDate dateTo);

	List<CustomerBonusRow> getCustomerBonusSummary(Long customerId);
}