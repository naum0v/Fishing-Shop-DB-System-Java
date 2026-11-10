package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.ProductSupply;
import java.util.List;

public interface ProductSupplyDAO {
	void receiveSupply(ProductSupply supply, Integer quantity);

	void updateSupplyPrice(Long supplyId, java.math.BigDecimal newPrice);

	void removeSupply(Long supplyId);

	List<ProductSupply> getAllSupplies();
}