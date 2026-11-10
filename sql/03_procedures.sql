-- 1. Добавление покупателя
CREATE OR REPLACE PROCEDURE add_customer(
    p_full_name VARCHAR,
    p_phone VARCHAR,
    p_email VARCHAR,
    p_address VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO Customers (full_name, phone, email, address)
    VALUES (p_full_name, p_phone, p_email, p_address);
END;
$$;

-- 2. Обновление данных покупателя
CREATE OR REPLACE PROCEDURE update_customer(
    p_customer_id BIGINT,
    p_full_name VARCHAR,
    p_phone VARCHAR,
    p_email VARCHAR,
    p_address VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE Customers
    SET full_name = p_full_name,
        phone = p_phone,
        email = p_email,
        address = p_address
    WHERE customer_id = p_customer_id;
END;
$$;

-- 3. Удаление покупателя (с проверкой наличия заказов и карт)
CREATE OR REPLACE PROCEDURE delete_customer(p_customer_id BIGINT)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM Orders WHERE customer_id = p_customer_id) THEN
        RAISE EXCEPTION 'Невозможно удалить покупателя - у него есть заказы.';
    END IF;
    IF EXISTS (SELECT 1 FROM LoyaltyCards WHERE customer_id = p_customer_id) THEN
        RAISE EXCEPTION 'Невозможно удалить покупателя - у него есть карта лояльности.';
    END IF;
    DELETE FROM Customers WHERE customer_id = p_customer_id;
END;
$$;

-- 4. Регистрация новой карты для покупателя
CREATE OR REPLACE PROCEDURE add_loyalty_card(
    p_card_number VARCHAR,
    p_registration_date DATE,
    p_accumulated_points NUMERIC,
    p_customer_id BIGINT,
    p_level_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM LoyaltyCards WHERE customer_id = p_customer_id) THEN
        RAISE EXCEPTION 'У этого покупателя уже есть карта лояльности.';
    END IF;
    INSERT INTO LoyaltyCards (card_number, registration_date, accumulated_points, customer_id, level_id)
    VALUES (p_card_number, p_registration_date, p_accumulated_points, p_customer_id, p_level_id);
END;
$$;

-- 5. Обновление карты лояльности (уровень или блокировка)
CREATE OR REPLACE PROCEDURE update_loyalty_card(
    p_loyalty_card_id BIGINT,
    p_level_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE LoyaltyCards
    SET level_id = p_level_id
    WHERE loyalty_card_id = p_loyalty_card_id;
END;
$$;

-- 6. Удаление карты лояльности
CREATE OR REPLACE PROCEDURE delete_loyalty_card(p_loyalty_card_id BIGINT)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM LoyaltyCards WHERE loyalty_card_id = p_loyalty_card_id;
END;
$$;

-- 7. Добавление нового уровня
CREATE OR REPLACE PROCEDURE add_card_level(
    p_level_name VARCHAR,
    p_discount_percent NUMERIC,
    p_bonus_multiplier NUMERIC
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO CardLevels (level_name, discount_percent, bonus_multiplier)
    VALUES (p_level_name, p_discount_percent, p_bonus_multiplier);
END;
$$;

-- 8. Обновление параметров уровня
CREATE OR REPLACE PROCEDURE update_card_level(
    p_level_id BIGINT,
    p_level_name VARCHAR,
    p_discount_percent NUMERIC,
    p_bonus_multiplier NUMERIC
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE CardLevels
    SET level_name = p_level_name,
        discount_percent = p_discount_percent,
        bonus_multiplier = p_bonus_multiplier
    WHERE level_id = p_level_id;
END;
$$;

-- 9. Удаление уровня (если нет карт с этим уровнем)
CREATE OR REPLACE PROCEDURE delete_card_level(p_level_id BIGINT)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM LoyaltyCards WHERE level_id = p_level_id) THEN
        RAISE EXCEPTION 'Невозможно удалить уровень - есть карты с этим уровнем.';
    END IF;
    DELETE FROM CardLevels WHERE level_id = p_level_id;
END;
$$;

-- 10. Добавление продавца
CREATE OR REPLACE PROCEDURE add_seller(
    p_full_name VARCHAR,
    p_phone VARCHAR,
    p_hire_date DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO Sellers (full_name, phone, hire_date)
    VALUES (p_full_name, p_phone, p_hire_date);
END;
$$;

-- 11. Обновление данных продавца
CREATE OR REPLACE PROCEDURE update_seller(
    p_seller_id BIGINT,
    p_full_name VARCHAR,
    p_phone VARCHAR,
    p_hire_date DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE Sellers
    SET full_name = p_full_name,
        phone = p_phone,
        hire_date = p_hire_date
    WHERE seller_id = p_seller_id;
END;
$$;

-- 12. Добавление категории
CREATE OR REPLACE PROCEDURE add_category(
    p_category_name VARCHAR,
    p_description TEXT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO ProductCategories (category_name, description)
    VALUES (p_category_name, p_description);
END;
$$;

-- 13. Обновление категории
CREATE OR REPLACE PROCEDURE update_category(
    p_category_id BIGINT,
    p_category_name VARCHAR,
    p_description TEXT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE ProductCategories
    SET category_name = p_category_name,
        description = p_description
    WHERE category_id = p_category_id;
END;
$$;

-- 14. Добавление товара
CREATE OR REPLACE PROCEDURE add_product(
    p_product_name VARCHAR,
    p_description TEXT,
    p_price NUMERIC,
    p_stock_quantity INTEGER,
    p_category_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO Products (product_name, description, price, stock_quantity, category_id)
    VALUES (p_product_name, p_description, p_price, p_stock_quantity, p_category_id);
END;
$$;

-- 15. Обновление товара
CREATE OR REPLACE PROCEDURE update_product(
    p_product_id BIGINT,
    p_product_name VARCHAR,
    p_description TEXT,
    p_price NUMERIC,
    p_stock_quantity INTEGER,
    p_category_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE Products
    SET product_name = p_product_name,
        description = p_description,
        price = p_price,
        stock_quantity = p_stock_quantity,
        category_id = p_category_id
    WHERE product_id = p_product_id;
END;
$$;

-- 16. Добавление поставщика
CREATE OR REPLACE PROCEDURE add_supplier(
    p_company_name VARCHAR,
    p_contact_person VARCHAR,
    p_phone VARCHAR,
    p_address VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO Suppliers (company_name, contact_person, phone, address)
    VALUES (p_company_name, p_contact_person, p_phone, p_address);
END;
$$;

-- 17. Обновление данных поставщика
CREATE OR REPLACE PROCEDURE update_supplier(
    p_supplier_id BIGINT,
    p_company_name VARCHAR,
    p_contact_person VARCHAR,
    p_phone VARCHAR,
    p_address VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE Suppliers
    SET company_name = p_company_name,
        contact_person = p_contact_person,
        phone = p_phone,
        address = p_address
    WHERE supplier_id = p_supplier_id;
END;
$$;

-- 18. Создание нового заказа
CREATE OR REPLACE PROCEDURE create_order(
    p_customer_id BIGINT,
    p_seller_id BIGINT,
    p_order_date_time TIMESTAMP,
    p_order_status VARCHAR,
    p_total_amount NUMERIC,
    p_items JSONB
)
LANGUAGE plpgsql AS $$
DECLARE
    v_order_id BIGINT;
    v_item JSONB;
    v_product_id BIGINT;
    v_quantity INTEGER;
    v_unit_price NUMERIC;
    v_available INTEGER;
BEGIN
    INSERT INTO Orders (order_date_time, order_status, total_amount, customer_id, seller_id)
    VALUES (p_order_date_time, p_order_status, p_total_amount, p_customer_id, p_seller_id)
    RETURNING order_id INTO v_order_id;

    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items)
    LOOP
        v_product_id := (v_item->>'product_id')::BIGINT;
        v_quantity := (v_item->>'quantity')::INTEGER;

        SELECT stock_quantity INTO v_available FROM Products WHERE product_id = v_product_id;
        IF v_available < v_quantity THEN
            RAISE EXCEPTION 'Недостаточно товара (ID: %) на складе. Доступно: %, требуется: %',
                v_product_id, v_available, v_quantity;
        END IF;

        SELECT price INTO v_unit_price FROM Products WHERE product_id = v_product_id;

        INSERT INTO OrderItems (order_id, product_id, quantity, unit_price)
        VALUES (v_order_id, v_product_id, v_quantity, v_unit_price);

        UPDATE Products SET stock_quantity = stock_quantity - v_quantity WHERE product_id = v_product_id;
    END LOOP;
END;
$$;

-- 19. Добавление позиции в существующий заказ
CREATE OR REPLACE PROCEDURE add_order_item(
    p_order_id BIGINT,
    p_product_id BIGINT,
    p_quantity INTEGER
)
LANGUAGE plpgsql AS $$
DECLARE
    v_available INTEGER;
    v_unit_price NUMERIC;
    v_order_status VARCHAR;
BEGIN
    SELECT order_status INTO v_order_status FROM Orders WHERE order_id = p_order_id;
    IF v_order_status IN ('Оплачен', 'Выполнен') THEN
        RAISE EXCEPTION 'Нельзя добавить позицию в оплаченный или выполненный заказ.';
    END IF;

    SELECT stock_quantity INTO v_available FROM Products WHERE product_id = p_product_id;
    IF v_available < p_quantity THEN
        RAISE EXCEPTION 'Недостаточно товара (ID: %) на складе. Доступно: %, требуется: %',
            p_product_id, v_available, p_quantity;
    END IF;

    SELECT price INTO v_unit_price FROM Products WHERE product_id = p_product_id;

    INSERT INTO OrderItems (order_id, product_id, quantity, unit_price)
    VALUES (p_order_id, p_product_id, p_quantity, v_unit_price);

    UPDATE Products SET stock_quantity = stock_quantity - p_quantity WHERE product_id = p_product_id;

    UPDATE Orders
    SET total_amount = (
        SELECT SUM(quantity * unit_price) FROM OrderItems WHERE order_id = p_order_id
    )
    WHERE order_id = p_order_id;
END;
$$;

-- 20. Удаление позиции из заказа
CREATE OR REPLACE PROCEDURE remove_order_item(
    p_order_id BIGINT,
    p_product_id BIGINT
)
LANGUAGE plpgsql AS $$
DECLARE
    v_quantity INTEGER;
    v_order_status VARCHAR;
BEGIN
    SELECT order_status INTO v_order_status FROM Orders WHERE order_id = p_order_id;
    IF v_order_status IN ('Оплачен', 'Выполнен') THEN
        RAISE EXCEPTION 'Нельзя удалить позицию из оплаченного или выполненного заказа.';
    END IF;

    SELECT quantity INTO v_quantity FROM OrderItems
    WHERE order_id = p_order_id AND product_id = p_product_id;

    IF v_quantity IS NULL THEN
        RAISE EXCEPTION 'Позиция не найдена в заказе.';
    END IF;

    UPDATE Products SET stock_quantity = stock_quantity + v_quantity
    WHERE product_id = p_product_id;

    DELETE FROM OrderItems WHERE order_id = p_order_id AND product_id = p_product_id;

    UPDATE Orders
    SET total_amount = (
        SELECT COALESCE(SUM(quantity * unit_price), 0) FROM OrderItems WHERE order_id = p_order_id
    )
    WHERE order_id = p_order_id;
END;
$$;

-- 21. Изменение статуса заказа
CREATE OR REPLACE PROCEDURE update_order_status(
    p_order_id BIGINT,
    p_new_status VARCHAR
)
LANGUAGE plpgsql AS $$
DECLARE
    v_old_status VARCHAR;
    v_customer_id BIGINT;
    v_total_amount NUMERIC;
    v_bonus_multiplier NUMERIC;
    v_bonus_points NUMERIC;
BEGIN
    SELECT order_status, customer_id, total_amount 
    INTO v_old_status, v_customer_id, v_total_amount
    FROM Orders WHERE order_id = p_order_id;

    IF v_old_status IS NULL THEN
        RAISE EXCEPTION 'Заказ не найден.';
    END IF;

    IF v_old_status IN ('Выполнен', 'Отменён') THEN
        RAISE EXCEPTION 'Нельзя изменить статус выполненного или отменённого заказа.';
    END IF;

    IF p_new_status = 'Оплачен' AND v_old_status = 'Оформлен' THEN
        NULL;
    ELSIF p_new_status = 'Выполнен' AND v_old_status = 'Оплачен' THEN
        SELECT c.bonus_multiplier INTO v_bonus_multiplier
        FROM LoyaltyCards l
        JOIN CardLevels c ON l.level_id = c.level_id
        WHERE l.customer_id = v_customer_id;

        IF v_bonus_multiplier IS NOT NULL THEN
            v_bonus_points := v_total_amount * v_bonus_multiplier;
            UPDATE LoyaltyCards
            SET accumulated_points = accumulated_points + v_bonus_points
            WHERE customer_id = v_customer_id;
        END IF;
    ELSIF p_new_status = 'Отменён' THEN
        UPDATE Products
        SET stock_quantity = stock_quantity + oi.quantity
        FROM OrderItems oi
        WHERE oi.order_id = p_order_id AND Products.product_id = oi.product_id;
    ELSE
        RAISE EXCEPTION 'Недопустимый переход статуса: % -> %', v_old_status, p_new_status;
    END IF;

    UPDATE Orders SET order_status = p_new_status WHERE order_id = p_order_id;
END;
$$;

-- 22. Отмена заказа
CREATE OR REPLACE PROCEDURE cancel_order(p_order_id BIGINT)
LANGUAGE plpgsql AS $$
DECLARE
    v_status VARCHAR;
BEGIN
    SELECT order_status INTO v_status FROM Orders WHERE order_id = p_order_id;
    IF v_status IN ('Выполнен') THEN
        RAISE EXCEPTION 'Нельзя отменить выполненный заказ.';
    END IF;

    CALL update_order_status(p_order_id, 'Отменён');
END;
$$;

-- 23. Применение скидки по карте лояльности к заказу
CREATE OR REPLACE PROCEDURE apply_discount_to_order(
    p_order_id BIGINT,
    p_discount_percent NUMERIC
)
LANGUAGE plpgsql AS $$
DECLARE
    v_current_total NUMERIC;
BEGIN
    SELECT total_amount INTO v_current_total FROM Orders WHERE order_id = p_order_id;
    IF v_current_total IS NULL THEN
        RAISE EXCEPTION 'Заказ не найден.';
    END IF;

    UPDATE Orders
    SET total_amount = v_current_total * (1 - p_discount_percent / 100)
    WHERE order_id = p_order_id;
END;
$$;

-- 24. Вычисление начисляемых баллов для покупателя
CREATE OR REPLACE FUNCTION calculate_bonus_points(
    p_customer_id BIGINT,
    p_order_amount NUMERIC
)
RETURNS NUMERIC
LANGUAGE plpgsql AS $$
DECLARE
    v_multiplier NUMERIC;
BEGIN
    SELECT c.bonus_multiplier INTO v_multiplier
    FROM LoyaltyCards l
    JOIN CardLevels c ON l.level_id = c.level_id
    WHERE l.customer_id = p_customer_id;

    IF v_multiplier IS NULL THEN
        RETURN 0;
    END IF;

    RETURN p_order_amount * v_multiplier;
END;
$$;

-- 25. Списание баллов при оплате заказа
CREATE OR REPLACE PROCEDURE apply_bonus_points(
    p_customer_id BIGINT,
    p_points_to_spend NUMERIC
)
LANGUAGE plpgsql AS $$
DECLARE
    v_available_points NUMERIC;
BEGIN
    SELECT accumulated_points INTO v_available_points
    FROM LoyaltyCards WHERE customer_id = p_customer_id;

    IF v_available_points < p_points_to_spend THEN
        RAISE EXCEPTION 'Недостаточно баллов. Доступно: %, требуется: %',
            v_available_points, p_points_to_spend;
    END IF;

    UPDATE LoyaltyCards
    SET accumulated_points = accumulated_points - p_points_to_spend
    WHERE customer_id = p_customer_id;
END;
$$;

-- 26. Автоматическое повышение уровня карты при достижении суммы покупок
CREATE OR REPLACE PROCEDURE update_card_level_by_spending(p_customer_id BIGINT)
LANGUAGE plpgsql AS $$
DECLARE
    v_total_spent NUMERIC;
    v_new_level_id BIGINT;
    v_current_level_id BIGINT;
BEGIN
    SELECT COALESCE(SUM(total_amount), 0) INTO v_total_spent
    FROM Orders
    WHERE customer_id = p_customer_id AND order_status = 'Выполнен';

    IF v_total_spent >= 50000 THEN
        SELECT level_id INTO v_new_level_id FROM CardLevels WHERE level_name = 'Платиновая';
    ELSIF v_total_spent >= 35000 THEN
        SELECT level_id INTO v_new_level_id FROM CardLevels WHERE level_name = 'Премиум';
    ELSIF v_total_spent >= 20000 THEN
        SELECT level_id INTO v_new_level_id FROM CardLevels WHERE level_name = 'Золотая';
    ELSIF v_total_spent >= 10000 THEN
        SELECT level_id INTO v_new_level_id FROM CardLevels WHERE level_name = 'Серебряная';
    ELSE
        SELECT level_id INTO v_new_level_id FROM CardLevels WHERE level_name = 'Стандарт';
    END IF;

    SELECT level_id INTO v_current_level_id FROM LoyaltyCards WHERE customer_id = p_customer_id;

    IF v_new_level_id IS NOT NULL AND v_new_level_id != v_current_level_id THEN
        UPDATE LoyaltyCards SET level_id = v_new_level_id WHERE customer_id = p_customer_id;
    END IF;
END;
$$;

-- 27. Приёмка поставки
CREATE OR REPLACE PROCEDURE receive_supply(
    p_supplier_id BIGINT,
    p_product_id BIGINT,
    p_last_supply_date DATE,
    p_purchase_price NUMERIC,
    p_quantity INTEGER,
    p_is_primary_supplier BOOLEAN
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO ProductSupplies (supplier_id, product_id, last_supply_date, purchase_price, is_primary_supplier)
    VALUES (p_supplier_id, p_product_id, p_last_supply_date, p_purchase_price, p_is_primary_supplier);

    UPDATE Products
    SET stock_quantity = stock_quantity + p_quantity
    WHERE product_id = p_product_id;
END;
$$;

-- 28. Корректировка закупочной цены
CREATE OR REPLACE PROCEDURE update_supply_price(
    p_product_supply_id BIGINT,
    p_new_price NUMERIC
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE ProductSupplies
    SET purchase_price = p_new_price
    WHERE product_supply_id = p_product_supply_id;
END;
$$;

-- 29. Удаление записи о поставке
CREATE OR REPLACE PROCEDURE remove_supply(p_product_supply_id BIGINT)
LANGUAGE plpgsql AS $$
DECLARE
    v_product_id BIGINT;
    v_quantity INTEGER;
BEGIN
    SELECT product_id, quantity INTO v_product_id, v_quantity
    FROM ProductSupplies
    WHERE product_supply_id = p_product_supply_id;

    IF v_product_id IS NULL THEN
        RAISE EXCEPTION 'Поставка не найдена.';
    END IF;

    UPDATE Products SET stock_quantity = stock_quantity - v_quantity
    WHERE product_id = v_product_id;

    DELETE FROM ProductSupplies WHERE product_supply_id = p_product_supply_id;
END;
$$;

-- 30. Отчёт по продажам за период
CREATE OR REPLACE FUNCTION get_sales_report(
    p_date_from DATE,
    p_date_to DATE
)
RETURNS TABLE(
    order_date DATE,
    order_count BIGINT,
    total_sales NUMERIC,
    avg_check NUMERIC
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        DATE(o.order_date_time) AS order_date,
        COUNT(o.order_id) AS order_count,
        SUM(o.total_amount) AS total_sales,
        AVG(o.total_amount) AS avg_check
    FROM Orders o
    WHERE DATE(o.order_date_time) BETWEEN p_date_from AND p_date_to
      AND o.order_status = 'Выполнен'
    GROUP BY DATE(o.order_date_time)
    ORDER BY order_date;
END;
$$;

-- 31. Отчёт по эффективности поставщиков
CREATE OR REPLACE FUNCTION get_supplier_efficiency(
    p_date_from DATE,
    p_date_to DATE
)
RETURNS TABLE(
    supplier_name VARCHAR,
    total_supplies BIGINT,
    total_purchase NUMERIC,
    avg_price NUMERIC
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        s.company_name,
        COUNT(ps.product_supply_id) AS total_supplies,
        SUM(ps.purchase_price) AS total_purchase,
        AVG(ps.purchase_price) AS avg_price
    FROM Suppliers s
    JOIN ProductSupplies ps ON s.supplier_id = ps.supplier_id
    WHERE ps.last_supply_date BETWEEN p_date_from AND p_date_to
    GROUP BY s.company_name
    ORDER BY total_purchase DESC;
END;
$$;

-- 32. Сводка по баллам и покупкам конкретного покупателя
CREATE OR REPLACE FUNCTION get_customer_bonus_summary(p_customer_id BIGINT)
RETURNS TABLE(
    customer_name VARCHAR,
    card_number VARCHAR,
    level_name VARCHAR,
    accumulated_points NUMERIC,
    total_purchases NUMERIC,
    total_orders BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        c.full_name,
        lc.card_number,
        cl.level_name,
        lc.accumulated_points,
        COALESCE(SUM(o.total_amount), 0) AS total_purchases,
        COUNT(o.order_id) AS total_orders
    FROM Customers c
    LEFT JOIN LoyaltyCards lc ON c.customer_id = lc.customer_id
    LEFT JOIN CardLevels cl ON lc.level_id = cl.level_id
    LEFT JOIN Orders o ON c.customer_id = o.customer_id AND o.order_status = 'Выполнен'
    WHERE c.customer_id = p_customer_id
    GROUP BY c.full_name, lc.card_number, cl.level_name, lc.accumulated_points;
END;
$$;