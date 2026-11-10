package ru.rsreu.fishingshop.dao;

public abstract class DAOFactory {
	public static DAOFactory getInstance(DBType dbType) {
		return dbType.getDAOFactory();
	}

	public abstract CustomerDAO getCustomerDAO();

	public abstract ProductDAO getProductDAO();

	public abstract OrderDAO getOrderDAO();

	public abstract OrderItemDAO getOrderItemDAO();

	public abstract SupplierDAO getSupplierDAO();

	public abstract CategoryDAO getCategoryDAO();

	public abstract LoyaltyCardDAO getLoyaltyCardDAO();

	public abstract CardLevelDAO getCardLevelDAO();

	public abstract SellerDAO getSellerDAO();

	public abstract ProductSupplyDAO getProductSupplyDAO();

	public abstract ReportDAO getReportDAO();
}