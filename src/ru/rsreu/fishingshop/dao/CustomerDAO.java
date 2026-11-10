package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.Customer;
import java.util.List;

public interface CustomerDAO {
	void addCustomer(Customer customer);

	void updateCustomer(Customer customer);

	void deleteCustomer(Long id);

	List<Customer> getAllCustomers();
}