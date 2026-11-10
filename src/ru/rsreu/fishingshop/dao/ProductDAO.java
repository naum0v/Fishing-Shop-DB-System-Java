package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.Product;
import java.util.List;

public interface ProductDAO {
	void addProduct(Product product);

	void updateProduct(Product product);

	List<Product> getAllProducts();
}