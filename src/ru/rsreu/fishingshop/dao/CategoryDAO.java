package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.Category;
import java.util.List;

public interface CategoryDAO {
	void addCategory(Category category);

	void updateCategory(Category category);

	void deleteCategory(Long id);

	List<Category> getAllCategories();
}