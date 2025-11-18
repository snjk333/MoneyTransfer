package com.oleksandr.moneytransfer.exceptions;

public class SelfTransferException extends RuntimeException {
    public SelfTransferException(String message) {
        super(message);
    }
}
