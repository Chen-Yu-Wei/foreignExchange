package com.example.foreignexchange;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

// 處理資料庫新增、查詢、更新、刪除class
@Repository
public class JdbcExchangeRateRepository implements ExchangeRateRepository {

    private final JdbcTemplate jdbcTemplate;

    // 建構函式，注入 JdbcTemplate 實例
    public JdbcExchangeRateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 查詢所有日期的方法
    @Override
    public List<String> findAllDates() {
        String sql = "SELECT DISTINCT date FROM exchange_rate";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    // 根據日期查詢匯率資料的方法
    @Override
    public ExchangeRate findByDate(String date) {
        try {
            String sql = "SELECT * FROM exchange_rate WHERE date = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{date}, new ExchangeRateRowMapper());
        } catch (EmptyResultDataAccessException e) {
            // 找不到對應日期的匯率資料，返回 null
            return null;
        }
    }

    // 根據日期刪除匯率資料的方法
    @Override
    public void deleteByDate(String date) {
        String sql = "DELETE FROM exchange_rate WHERE date = ?";
        jdbcTemplate.update(sql, date);
    }

    // 新增匯率資料的方法
    @Override
    public void save(ExchangeRate exchangeRate) {
        String sql = "INSERT INTO exchange_rate (date, usd_to_ntd, rmb_to_ntd, usd_to_rmb) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, exchangeRate.getDate(), exchangeRate.getUsdToNtd(), exchangeRate.getRmbToNtd(), exchangeRate.getUsdToRmb());
    }

    // 更新匯率資料的方法
    @Override
    public void update(ExchangeRate exchangeRate) {
        String sql = "UPDATE exchange_rate SET usd_to_ntd = ?, rmb_to_ntd = ?, usd_to_rmb = ? WHERE date = ?";
        jdbcTemplate.update(sql, exchangeRate.getUsdToNtd(), exchangeRate.getRmbToNtd(), exchangeRate.getUsdToRmb(), exchangeRate.getDate());
    }
}
