-- 1. Триггер для пересчёта суммы заказа
CREATE OR REPLACE FUNCTION fn_update_order_total()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Orders
    SET total_amount = (
        SELECT COALESCE(SUM(quantity * unit_price), 0)
        FROM OrderItems
        WHERE order_id = COALESCE(NEW.order_id, OLD.order_id)
    )
    WHERE order_id = COALESCE(NEW.order_id, OLD.order_id);
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_order_total ON OrderItems;
CREATE TRIGGER trg_update_order_total
AFTER INSERT OR UPDATE OR DELETE ON OrderItems
FOR EACH ROW
EXECUTE FUNCTION fn_update_order_total();

-- 2. Триггер для проверки остатков перед добавлением позиции
CREATE OR REPLACE FUNCTION fn_check_stock_before_insert()
RETURNS TRIGGER AS $$
DECLARE
    v_available INTEGER;
BEGIN
    SELECT stock_quantity INTO v_available
    FROM Products
    WHERE product_id = NEW.product_id;

    IF v_available < NEW.quantity THEN
        RAISE EXCEPTION 'Недостаточно товара (ID: %) на складе. Доступно: %, требуется: %',
            NEW.product_id, v_available, NEW.quantity;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_check_stock_before_insert ON OrderItems;
CREATE TRIGGER trg_check_stock_before_insert
BEFORE INSERT ON OrderItems
FOR EACH ROW
EXECUTE FUNCTION fn_check_stock_before_insert();

-- 3. Триггер для контроля основного поставщика
CREATE OR REPLACE FUNCTION fn_check_primary_supplier()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_primary_supplier = TRUE THEN
        UPDATE ProductSupplies
        SET is_primary_supplier = FALSE
        WHERE product_id = NEW.product_id
          AND product_supply_id != NEW.product_supply_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_check_primary_supplier ON ProductSupplies;
CREATE TRIGGER trg_check_primary_supplier
AFTER INSERT OR UPDATE OF is_primary_supplier ON ProductSupplies
FOR EACH ROW
EXECUTE FUNCTION fn_check_primary_supplier();