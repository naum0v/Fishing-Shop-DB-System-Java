package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.Seller;
import java.util.List;

public interface SellerDAO {
	void addSeller(Seller seller);

	void updateSeller(Seller seller);

	List<Seller> getAllSellers();
}