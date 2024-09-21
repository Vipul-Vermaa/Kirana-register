package com.example.kirana.Model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Document(collection = "transaction")

public class TransactionModel {
    //Getters and Setters
    @Id
    private String id;

    @NotNull(message = "Amount can not be null")
    private BigDecimal amount;

    @NotNull(message = "Enter the currency of the amount")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    private String currency;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    private LocalDateTime timestamp;

    public enum TransactionType {
        CREDIT, DEBIT
    }

}
