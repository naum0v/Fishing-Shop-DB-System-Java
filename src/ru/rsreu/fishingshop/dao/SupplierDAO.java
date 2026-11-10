package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.Supplier;
import java.util.List;

public interface SupplierDAO {
	void addSupplier(Supplier supplier);

	void updateSupplier(Supplier supplier);

	void deleteSupplier(Long id);

	List<Supplier> getAllSuppliers();
}