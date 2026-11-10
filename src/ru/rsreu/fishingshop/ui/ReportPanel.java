package ru.rsreu.fishingshop.ui;

import ru.rsreu.fishingshop.dao.DAOFactory;
import ru.rsreu.fishingshop.model.SalesReportRow;
import ru.rsreu.fishingshop.model.SupplierEfficiencyRow;
import ru.rsreu.fishingshop.model.CustomerBonusRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReportPanel extends JPanel {
    private final DAOFactory factory;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField dateFromField, dateToField, customerIdField;
    private JComboBox<String> reportTypeCombo;

    public ReportPanel(DAOFactory factory) {
        this.factory = factory;
        setLayout(new BorderLayout(10, 10));
        initControls();
        initTable();

        LocalDate today = LocalDate.now();
        dateFromField.setText(today.withDayOfMonth(1).toString());
        dateToField.setText(today.toString());

        loadReport("sales");
    }

    private void initControls() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reportTypeCombo = new JComboBox<>(new String[]{"Продажи", "Поставщики", "Бонусы покупателя"});
        dateFromField = new JTextField(10);
        dateToField = new JTextField(10);
        customerIdField = new JTextField(10);
        JButton showBtn = new JButton("Показать");

        showBtn.addActionListener(this::onShow);

        controlPanel.add(new JLabel("Отчёт:"));
        controlPanel.add(reportTypeCombo);
        controlPanel.add(new JLabel("С даты:"));
        controlPanel.add(dateFromField);
        controlPanel.add(new JLabel("По дату:"));
        controlPanel.add(dateToField);
        controlPanel.add(new JLabel("ID покупателя:"));
        controlPanel.add(customerIdField);
        controlPanel.add(showBtn);

        add(controlPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void onShow(ActionEvent e) {
        String selected = (String) reportTypeCombo.getSelectedItem();
        if ("Продажи".equals(selected)) {
            loadReport("sales");
        } else if ("Поставщики".equals(selected)) {
            loadReport("suppliers");
        } else if ("Бонусы покупателя".equals(selected)) {
            loadReport("bonus");
        }
    }

    private void loadReport(String type) {
        try {
            String fromText = dateFromField.getText().trim();
            String toText = dateToField.getText().trim();

            if (fromText.isEmpty() || toText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните даты.");
                return;
            }

            LocalDate dateFrom = LocalDate.parse(fromText);
            LocalDate dateTo = LocalDate.parse(toText);

            if ("sales".equals(type)) {
                List<SalesReportRow> rows = factory.getReportDAO().getSalesReport(dateFrom, dateTo);
                tableModel.setColumnIdentifiers(new Object[]{"Дата", "Кол-во заказов", "Сумма", "Средний чек"});
                tableModel.setRowCount(0);
                for (SalesReportRow r : rows) {
                    tableModel.addRow(new Object[]{r.orderDate(), r.orderCount(), r.totalSales(), r.avgCheck()});
                }
            } else if ("suppliers".equals(type)) {
                List<SupplierEfficiencyRow> rows = factory.getReportDAO().getSupplierEfficiency(dateFrom, dateTo);
                tableModel.setColumnIdentifiers(new Object[]{"Поставщик", "Поставок", "Закуплено", "Средняя цена"});
                tableModel.setRowCount(0);
                for (SupplierEfficiencyRow r : rows) {
                    tableModel.addRow(new Object[]{r.supplierName(), r.totalSupplies(), r.totalPurchase(), r.avgPrice()});
                }
            } else if ("bonus".equals(type)) {
                String customerIdText = customerIdField.getText().trim();
                if (customerIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введите ID покупателя.");
                    return;
                }
                Long customerId = Long.parseLong(customerIdText);
                List<CustomerBonusRow> rows = factory.getReportDAO().getCustomerBonusSummary(customerId);
                tableModel.setColumnIdentifiers(new Object[]{"Покупатель", "Номер карты", "Уровень", "Баллы", "Покупок на сумму", "Заказов"});
                tableModel.setRowCount(0);
                for (CustomerBonusRow r : rows) {
                    tableModel.addRow(new Object[]{r.customerName(), r.cardNumber(), r.levelName(),
                            r.accumulatedPoints(), r.totalPurchases(), r.totalOrders()});
                }
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты. Используйте YYYY-MM-DD.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректный ID покупателя.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке отчёта: " + ex.getMessage());
        }
    }
}