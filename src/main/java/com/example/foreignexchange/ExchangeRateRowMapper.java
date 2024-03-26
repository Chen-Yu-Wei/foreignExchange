package com.example.foreignexchange;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

// ExchangeRateRowMapper類實現了Spring JDBC的RowMapper接口，用於映射ResultSet中的行到ExchangeRate對象
public class ExchangeRateRowMapper implements RowMapper<ExchangeRate> {

    // mapRow方法將ResultSet中的一行，也就是資料庫裡的每一欄位的內容，映射到ExchangeRate對象上
    @Override
    public ExchangeRate mapRow(ResultSet rs, int rowNum) throws SQLException {
        // 創建一個新的ExchangeRate對象
        ExchangeRate exchangeRate = new ExchangeRate();
        // 從ResultSet中提取日期(date)、美金 to 台幣(usd_to_ntd)、人民幣 to 台幣(rmb_to_ntd)、美金 to 人民幣(usd_to_rmb)等欄位的值，並設置到ExchangeRate對象上
        exchangeRate.setDate(rs.getString("date"));
        exchangeRate.setUsdToNtd(rs.getString("usd_to_ntd"));
        exchangeRate.setRmbToNtd(rs.getString("rmb_to_ntd"));
        exchangeRate.setUsdToRmb(rs.getString("usd_to_rmb"));
        return exchangeRate;
    }
}
