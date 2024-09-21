package com.example.kirana.Repository;

import com.example.kirana.Model.TransactionModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;

/**
 * Repository interface for handling CRUD operations and custom queries for transactions.
 * This interface extends MongoRepository to provide standard methods for interacting with MongoDB.
 */
@Repository
public interface TransactionRepository extends MongoRepository<TransactionModel, String> {
    /**
     * Finds a list of transactions based on their type (CREDIT or DEBIT).
     *
     * @param type The transaction type (CREDIT or DEBIT).
     * @return A list of transactions matching the specified type.
     */
    List<TransactionModel> findByType(TransactionModel.TransactionType type);
    /**
     * Finds a list of transactions based on the currency in which they were made.
     *
     * @param currency The currency code (e.g., INR, USD) for filtering the transactions.
     * @return A list of transactions matching the specified currency.
     */
    List<TransactionModel> findByCurrency(String currency);
    /**
     * Finds a list of transactions made within a specified time range.
     *
     * @param startDate The start date of the period to search for transactions.
     * @param endDate   The end date of the period to search for transactions.
     * @return A list of transactions made between the specified start and end dates.
     */

    List<TransactionModel> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}