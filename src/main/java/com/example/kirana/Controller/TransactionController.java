package com.example.kirana.Controller;

import com.example.kirana.Model.TransactionModel;
import com.example.kirana.Service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing transactions and generating financial reports.
 *
 * This controller provides endpoints for adding transactions and generating
 * financial reports (weekly, monthly, yearly).
 *
 * <p>
 *     Example usage:
 *     <ul>
 *         <li>POST /api/transactions/addtransaction - Add a transaction</li>
 *         <li>GET /api/transactions/reports - Generate financial reports</li>
 *     </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/transactions")
@Api(value = "Transaction Controller", description = "Operations related to transaction management and reporting.")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

/**
 * Adds a new transaction to the system.
 *
 * <p>This endpoint records a transaction with a specified amount, type, and currency.
 * The type can be either 'credit' or 'debit'. The transaction is stored in the database
 * and returned with a status of CREATED (201) if successful.</p>
 *
 * @param amount   The amount of the transaction.
 * @param type     The type of the transaction ('credit' or 'debit').
 * @param currency The currency in which the transaction was made (e.g., 'INR', 'USD').
 * @return A ResponseEntity containing the created transaction and an HTTP status code.
 *         If the request is invalid, a BAD REQUEST (400) status code is returned.
 * @apiNote POST /api/transactions/addtransaction
 * @example
 * <pre>
 *     * POST /api/transactions/addtransaction
 *      * Parameters:
 *      *   - amount=1000
 *      *   - type=credit
 *      *   - currency=USD
 *      * Response:
 *      *   HTTP/1.1 201 Created
 *      *   {
 *      *     "id": "12345",
 *      *     "amount": "1000",
 *      *     "type": "credit",
 *      *     "currency": "USD",
 *      *     "timestamp": "2024-09-21T12:34:56"
 *      *   }
 *      * </pre>
 *      */

@ApiOperation(value = "Add a new transaction", notes = "Add a transaction with the specified amount, type, and currency.")
    @PostMapping("/addtransaction")
    public ResponseEntity<TransactionModel> setTransaction(
        @ApiParam(value = "Amount of the transaction", required = true) @RequestParam BigDecimal amount,
        @ApiParam(value = "Type of the transaction (CREDIT or DEBIT)", required = true)    @RequestParam String type,
        @ApiParam(value = "Currency of the transaction (e.g., USD, INR)", required = true)    @RequestParam String currency) {
        try {
            TransactionModel transaction =transactionService.addTransaction(amount, type, currency);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }

/**
 * Generates a financial report based on the specified report type.
 *
 * <p>This endpoint generates a report for transactions over a specified time range.
 * It accepts three report types: 'weekly', 'monthly', and 'yearly'. The generated
 * report contains the list of transactions within the specified range.</p>
 *
 * @param type The type of report to generate ('weekly', 'monthly', or 'yearly').
 * @return A ResponseEntity containing the list of transactions and an HTTP status code.
 *         If an invalid report type is provided, a BAD REQUEST (400) status code is returned.
 * @apiNote GET /api/transactions/reports
 * @example
 * <pre>
 * GET /api/transactions/reports?type=monthly
 * Response:
 * *   HTTP/1.1 200 OK
 *      *   [
 *      *     {
 *      *       "id": "12345",
 *      *       "amount": "1000",
 *      *       "type": "credit",
 *      *       "currency": "USD",
 *      *       "timestamp": "2024-08-21T12:34:56"
 *      *     },
 *      *     {
 *      *       "id": "67890",
 *      *       "amount": "500",
 *      *       "type": "debit",
 *      *       "currency": "INR",
 *      *       "timestamp": "2024-08-15T09:20:00"
 *      *     }
 *      *   ]
 *      * </pre>
 *      */

    @ApiOperation(value = "Generate a financial report", notes = "Generates a financial report for a specified type (weekly, monthly, or yearly).")
    @GetMapping("/reports")
    public ResponseEntity<List<TransactionModel>> generateReport(
            @ApiParam(value = "Type of the report (weekly, monthly, yearly)", required = true)  @RequestParam String type
    ){
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (type.toLowerCase()) {
            case "weekly":
                startDate = endDate.minusWeeks(1);
                break;
            case "monthly":
                startDate = endDate.minusMonths(1);
                break;
            case "yearly":
                startDate = endDate.minusYears(1);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<TransactionModel> transactions = transactionService.getReports(startDate, endDate);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}

