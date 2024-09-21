package com.example.kirana.Service;
import com.example.kirana.Utils.RateLimitExceededException;
import com.google.common.util.concurrent.RateLimiter;

import com.example.kirana.Model.TransactionModel;
import com.example.kirana.Model.TransactionModel.TransactionType;
import com.example.kirana.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.kirana.Utils.InvalidTransactionTypeException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;

/**
 * Service to handle business logic related to transactions and report generation.
 * This includes adding new transactions, converting currencies, and generating financial reports.
 */
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;
//
    // RateLimiter to control the number of requests to the transaction service (10 requests per second).
    private final RateLimiter rateLimiter = RateLimiter.create(10);

    private static final String CURRENCY_CONVERSION_API_URL = "https://api.fxratesapi.com/latest?base={base}&symbols={target}";

    /**
     * Adds a new transaction to the system after performing currency conversion and validating the type.
     * This method is rate-limited to 10 requests per second.
     *
     * @param amount    The amount of the transaction.
     * @param type      The type of the transaction (CREDIT or DEBIT).
     * @param currency  The currency of the transaction (e.g., USD, EUR).
     * @return The saved TransactionModel object.
     * @throws RateLimitExceededException if too many requests are made.
     * @throws IllegalArgumentException   if the transaction type is invalid.
     */
    public TransactionModel addTransaction(BigDecimal amount, String type,String currency){
        if (!rateLimiter.tryAcquire()) {
            throw new RateLimitExceededException("Too many requests, please try again later.");
        }
        BigDecimal convertedAmount = convertCurrency(amount, currency, "INR");

        TransactionModel transaction=new TransactionModel();

        transaction.setAmount(convertedAmount);
        transaction.setCurrency("INR");

        TransactionType transactionType;
        try {
            transactionType = TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTransactionTypeException("Invalid transaction type: " + type);
        }
        transaction.setType(transactionType);

        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    /**
     * Converts the given amount from one currency to another using an external API.
     * Caching is applied to reduce redundant API calls for the same currency conversion.
     *
     * @param amount        The amount to convert.
     * @param fromCurrency  The source currency.
     * @param toCurrency    The target currency.
     * @return The converted amount in the target currency.
     * @throws RuntimeException if the conversion fails due to API issues.
     */
@Cacheable(value = "currencyConversions", key = "#fromCurrency + '_' + #toCurrency")
public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {

    RestTemplate restTemplate = new RestTemplate();
    String apiUrl = CURRENCY_CONVERSION_API_URL.replace("{base}", fromCurrency)
            .replace("{target}", toCurrency);


    Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

    if (response != null && response.containsKey("rates")) {
        Object ratesObj = response.get("rates");
        if (ratesObj instanceof Map<?, ?>) {
    Map<String, BigDecimal> rates = (Map<String, BigDecimal>) ratesObj;
    BigDecimal conversionRate = rates.get(toCurrency);


    return amount.multiply(conversionRate);

}}throw new RuntimeException("Error during currency conversion.");

    /**
     * Retrieves a list of transactions within a given date range.
     *
     * @param startDate The start date of the report.
     * @param endDate   The end date of the report.
     * @return A list of TransactionModel objects between the specified dates.
     */
//    public List<TransactionModel> getReports(String type, LocalDateTime startDate, LocalDateTime endDate) {
//        return transactionRepository.findByTimestampBetween(startDate, endDate);
//        List<TransactionModel> transactions = transactionRepository.findByTimestampBetween(startDate, endDate);

//        return transactions;
    }
    public List<TransactionModel> getReports(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTimestampBetween(startDate, endDate);
    }
    /**
     * Generates a financial report containing total credits, debits, and net flow for a given period.
     *
     * @param startDate The start date of the report.
     * @param endDate   The end date of the report.
     * @return A map containing the total credits, debits, and net flow.
     */
    public Map<String, BigDecimal> generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<TransactionModel> transactions = getReports(startDate, endDate);

        // Filter and sum credits and debits
        BigDecimal totalCredits = transactions.stream()
                .filter(t -> t.getType() == TransactionType.CREDIT)
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEBIT)
                .map(TransactionModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate net flow (Credits - Debits)
        BigDecimal netFlow = totalCredits.subtract(totalDebits);

        return Map.of(
                "totalCredits", totalCredits,
                "totalDebits", totalDebits,
                "netFlow", netFlow
        );
    }
}
