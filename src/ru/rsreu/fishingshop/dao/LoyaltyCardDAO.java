package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.model.LoyaltyCard;
import java.util.List;

public interface LoyaltyCardDAO {
	void addLoyaltyCard(LoyaltyCard card);

	void updateLoyaltyCard(Long cardId, Long levelId);

	void deleteLoyaltyCard(Long cardId);

	void applyBonusPoints(Long customerId, java.math.BigDecimal pointsToSpend);

	void updateCardLevelBySpending(Long customerId);

	List<LoyaltyCard> getAllLoyaltyCards();
}