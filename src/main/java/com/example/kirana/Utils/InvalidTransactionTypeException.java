package com.example.kirana.Utils;

public class InvalidTransactionTypeException extends RuntimeException{
    public InvalidTransactionTypeException(String message) {
        super(message);
    }
}
