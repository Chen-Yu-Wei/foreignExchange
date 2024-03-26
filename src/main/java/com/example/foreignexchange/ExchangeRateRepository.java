package com.example.foreignexchange;

import java.util.List;

// 介面定義了外匯匯率資料存取的方法
public interface ExchangeRateRepository {
    // 查詢所有日期
    List<String> findAllDates();

    // 根據日期查詢匯率
    ExchangeRate findByDate(String date);

    // 根據日期刪除匯率
    void deleteByDate(String date);

    // 儲存匯率到資料庫
    void save(ExchangeRate exchangeRate);

    // 更新匯率
    void update(ExchangeRate exchangeRate);
}
