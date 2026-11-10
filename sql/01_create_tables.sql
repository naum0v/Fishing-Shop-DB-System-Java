-- Покупатели
CREATE TABLE Customers (
    customer_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    address VARCHAR(250)
);

-- Уровни карт
CREATE TABLE CardLevels (
    level_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL UNIQUE,
    discount_percent NUMERIC(5,2) NOT NULL CHECK (discount_percent >= 0 AND discount_percent <= 100),
    bonus_multiplier NUMERIC(2,2) NOT NULL CHECK (bonus_multiplier > 0)
);

-- Карты лояльности
CREATE TABLE LoyaltyCards (
    loyalty_card_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    card_number VARCHAR(20) NOT NULL UNIQUE,
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
    accumulated_points NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (accumulated_points >= 0),
    customer_id BIGINT NOT NULL UNIQUE,
    level_id BIGINT NOT NULL,
    CONSTRAINT fk_loyalty_cards_customer FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE RESTRICT,
    CONSTRAINT fk_loyalty_cards_level FOREIGN KEY (level_id) REFERENCES CardLevels(level_id) ON DELETE RESTRICT
);

-- Продавцы
CREATE TABLE Sellers (
    seller_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    hire_date DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Категории товаров
CREATE TABLE ProductCategories (
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Поставщики
CREATE TABLE Suppliers (
    supplier_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL UNIQUE,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(250)
);

-- Товары
CREATE TABLE Products (
    product_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL CHECK (price > 0),
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES ProductCategories(category_id) ON DELETE RESTRICT
);

-- Заказы
CREATE TABLE Orders (
    order_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_date_time TIMESTAMP NOT NULL DEFAULT NOW(),
    order_status VARCHAR(20) NOT NULL CHECK (order_status IN ('Оформлен', 'Оплачен', 'Выполнен', 'Отменён')),
    total_amount NUMERIC(10,2) NOT NULL CHECK (total_amount >= 0),
    customer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_seller FOREIGN KEY (seller_id) REFERENCES Sellers(seller_id) ON DELETE RESTRICT
);

-- Состав заказа
CREATE TABLE OrderItems (
    order_item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10,2) NOT NULL CHECK (unit_price > 0),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE RESTRICT
);

-- Поставки товаров
CREATE TABLE ProductSupplies (
    product_supply_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    last_supply_date DATE NOT NULL DEFAULT CURRENT_DATE,
    purchase_price NUMERIC(10,2) NOT NULL CHECK (purchase_price > 0),
    is_primary_supplier BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_product_supplies_supplier FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE RESTRICT,
    CONSTRAINT fk_product_supplies_product FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE RESTRICT
);