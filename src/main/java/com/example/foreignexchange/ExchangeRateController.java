package com.example.foreignexchange;

import org.springframework.ui.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Controller
// 處理邏輯運算的conrtoller
public class ExchangeRateController {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    // GET方法，用於呼叫外匯匯率api，並初始化頁面
    @GetMapping("/exchangeRates")
    public String getExchangeRates(Model model) {
        // api網址
        String apiUrl = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";

        // 下載JSON文件，並寫進DailyForeignExchangeRates.json這個文件裡
        try {
            downloadJson(apiUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonFilePath = "DailyForeignExchangeRates.json";
        // 解析JSON文件並返回ExchangeRate對象的列表
        List<ExchangeRate> exchangeRates = parseJsonToExchangeRates(jsonFilePath);
        // 存進資料庫裡
        saveExchangeRatesToDatabase(exchangeRates);

        // 從資料庫中查詢所有日期並傳遞到前端，用於下拉選單
        List<String> dates = exchangeRateRepository.findAllDates();
        model.addAttribute("dates", dates);

        // 返回Thymeleaf模板的文件名
        return "exchangeRates";
    }

    // POST方法，用於新增外匯匯率
    @PostMapping("/addExchangeRate")
    public String addExchangeRate(@RequestParam String date,
                                  @RequestParam String usdToNtd,
                                  @RequestParam String rmbToNtd,
                                  @RequestParam String usdToRmb) {
        // 創一個ExchangeRate元件，並設置使用者輸入的值
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setDate(date);
        exchangeRate.setUsdToNtd(usdToNtd);
        exchangeRate.setRmbToNtd(rmbToNtd);
        exchangeRate.setUsdToRmb(usdToRmb);
        // 新增到資料庫裡
        exchangeRateRepository.save(exchangeRate);
        return "redirect:/exchangeRates";
    }

    // POST方法，用於更新外匯匯率
    @PostMapping("/updateExchangeRate")
    public String updateExchangeRate(@RequestParam String startDate,
                                     @RequestParam String usdToNtd,
                                     @RequestParam String rmbToNtd,
                                     @RequestParam String usdToRmb) {
        // 根據日期查詢要更新的外匯匯率
        ExchangeRate exchangeRate = exchangeRateRepository.findByDate(startDate);
        // 設置使用者輸入的匯率
        exchangeRate.setUsdToNtd(usdToNtd);
        exchangeRate.setRmbToNtd(rmbToNtd);
        exchangeRate.setUsdToRmb(usdToRmb);
        // 更新匯率
        exchangeRateRepository.update(exchangeRate);
        return "redirect:/exchangeRates";
    }

    // POST方法，用於刪除外匯匯率
    @PostMapping("/deleteExchangeRate")
    public String deleteExchangeRate(@RequestParam String startDate) {
        // 依照日期刪除匯率資料
        exchangeRateRepository.deleteByDate(startDate);
        return "redirect:/exchangeRates";
    }

    // GET方法，用於查詢指定日期的外匯匯率
    @GetMapping("/queryExchangeRates")
    public String queryExchangeRates(@RequestParam String startDate,
                                     Model model) {
        // 從資料庫中查詢所有日期並傳遞到前端，用於下拉選單
        List<String> dates = exchangeRateRepository.findAllDates();
        model.addAttribute("dates", dates);

        // 根據指定日期查詢外匯匯率
        ExchangeRate exchangeRates = exchangeRateRepository.findByDate(startDate);
        // 將查詢結果傳遞到前端
        //model.addAttribute("exchangeRates", exchangeRates);
        model.addAttribute("queryResult", exchangeRates);
        return "exchangeRates";
    }

    // 下載JSON文件的方法，因為api回傳的是一個JSON文件
    private void downloadJson(String url) throws IOException {
        // 創建 RestTemplate 實例，用於向指定的 URL 發送 HTTP 請求
        RestTemplate restTemplate = new RestTemplate();
        // 設置 HTTP 請求頭，指定接受的媒體類型為 JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // 創建 HttpEntity 對象，用於封裝header
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 向指定的 URL 發送 GET 請求，並接收 HTTP 響應內容為 byte 數組
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        // 將 HTTP 響應內容寫入到名稱為 DailyForeignExchangeRates.json 的文件中
        Files.write(Paths.get("DailyForeignExchangeRates.json"), response.getBody());
    }

    // 解析JSON並返回ExchangeRate對象的列表，使用的是Jackson，一個Json的類別庫
    private List<ExchangeRate> parseJsonToExchangeRates(String jsonFilePath) {

        // 初始化 ObjectMapper 物件，用於將 JSON 資料轉換為 Java 對象
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 讀取 JSON 檔案並將其轉換為 ExchangeRate[] 陣列
            ExchangeRate[] exchangeRates = objectMapper.readValue(Paths.get(jsonFilePath).toFile(), ExchangeRate[].class);
            // 將 ExchangeRate[] 陣列轉換為 List<ExchangeRate> 並返回
            return Arrays.asList(exchangeRates);
        } catch (IOException e) {
            // 如果解析過程中出現 IO 錯誤，返回 null
            e.printStackTrace();
            return null;
        }
    }

    // 將匯率資料保存到資料庫的方法
    private void saveExchangeRatesToDatabase(List<ExchangeRate> exchangeRates) {
        // 迴圈遍歷匯率列表中的每一個匯率對象
        for (ExchangeRate exchangeRate : exchangeRates) {
            // 根據日期查找資料庫中是否已存在該日期的匯率資料
            ExchangeRate existingRate = exchangeRateRepository.findByDate(exchangeRate.getDate());
            // 如果找不到對應日期的匯率資料，則將當前匯率資料插入到資料庫中
            if (existingRate == null) {
                exchangeRateRepository.save(exchangeRate);
            } else {
                // 如果找到了對應日期的匯率資料，則更新該匯率資料
                existingRate.setUsdToNtd(exchangeRate.getUsdToNtd());
                existingRate.setRmbToNtd(exchangeRate.getRmbToNtd());
                existingRate.setUsdToRmb(exchangeRate.getUsdToRmb());
                exchangeRateRepository.update(existingRate);
            }
        }
    }
}
