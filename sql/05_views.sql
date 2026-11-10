-- 1. Детали заказов
CREATE OR REPLACE VIEW view_order_details AS
SELECT
    o.order_id,
    o.order_date_time,
    c.full_name AS customer_name,
    s.full_name AS seller_name,
    o.order_status,
    o.total_amount,
    (SELECT COUNT(*) FROM OrderItems WHERE order_id = o.order_id) AS items_count
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
JOIN Sellers s ON o.seller_id = s.seller_id;

-- 2. Товары с категориями и остатками
CREATE OR REPLACE VIEW view_product_stock AS
SELECT
    p.product_id,
    p.product_name,
    pc.category_name,
    p.price,
    p.stock_quantity,
    (
        SELECT s.company_name
        FROM ProductSupplies ps
        JOIN Suppliers s ON ps.supplier_id = s.supplier_id
        WHERE ps.product_id = p.product_id AND ps.is_primary_supplier = TRUE
        LIMIT 1
    ) AS primary_supplier
FROM Products p
JOIN ProductCategories pc ON p.category_id = pc.category_id;

-- 3. Покупатели с картами и баллами
CREATE OR REPLACE VIEW view_customer_loyalty AS
SELECT
    c.customer_id,
    c.full_name,
    lc.card_number,
    cl.level_name,
    cl.discount_percent,
    lc.accumulated_points,
    COALESCE((
        SELECT SUM(total_amount)
        FROM Orders
        WHERE customer_id = c.customer_id AND order_status = 'Выполнен'
    ), 0) AS total_spent
FROM Customers c
LEFT JOIN LoyaltyCards lc ON c.customer_id = lc.customer_id
LEFT JOIN CardLevels cl ON lc.level_id = cl.level_id;

-- 4. Продажи по периодам
CREATE OR REPLACE VIEW view_sales_by_period AS
SELECT
    DATE(o.order_date_time) AS sale_date,
    COUNT(o.order_id) AS order_count,
    SUM(o.total_amount) AS total_sales,
    AVG(o.total_amount) AS avg_check
FROM Orders o
WHERE o.order_status = 'Выполнен'
GROUP BY DATE(o.order_date_time)
ORDER BY sale_date DESC;

-- 5. Эффективность поставщиков
CREATE OR REPLACE VIEW view_supplier_performance AS
SELECT
    s.supplier_id,
    s.company_name,
    COUNT(ps.product_supply_id) AS total_supplies,
    SUM(ps.purchase_price) AS total_purchase,
    AVG(ps.purchase_price) AS avg_price
FROM Suppliers s
JOIN ProductSupplies ps ON s.supplier_id = ps.supplier_id
GROUP BY s.supplier_id, s.company_name
ORDER BY total_purchase DESC;

-- 6. Топ 10 самых продаваемых товаров
CREATE OR REPLACE VIEW view_top_products AS
SELECT
    p.product_id,
    p.product_name,
    SUM(oi.quantity) AS total_quantity_sold,
    SUM(oi.quantity * oi.unit_price) AS total_revenue,
    RANK() OVER (ORDER BY SUM(oi.quantity) DESC) AS rank
FROM Products p
JOIN OrderItems oi ON p.product_id = oi.product_id
JOIN Orders o ON oi.order_id = o.order_id
WHERE o.order_status = 'Выполнен'
GROUP BY p.product_id, p.product_name
ORDER BY total_quantity_sold DESC
LIMIT 10;