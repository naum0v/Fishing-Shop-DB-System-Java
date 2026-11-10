package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.CardLevel;
import java.util.List;

public interface CardLevelDAO {
	void addCardLevel(CardLevel level);

	void updateCardLevel(CardLevel level);

	void deleteCardLevel(Long id);

	List<CardLevel> getAllCardLevels();
}