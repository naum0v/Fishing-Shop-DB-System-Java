package ru.rsreu.fishingshop.dao;

import ru.rsreu.fishingshop.dao.postgresql.PostgreSQLDAOFactory;

public enum DBType {
	POSTGRESQL {
		@Override
		public DAOFactory getDAOFactory() {
			return PostgreSQLDAOFactory.getInstance();
		}
	};

	public abstract DAOFactory getDAOFactory();
}