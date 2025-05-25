package org.store.exceptions;

public class NotEnoughStokaAvailableException extends RuntimeException {
    public NotEnoughStokaAvailableException(String stokaName, double quantity) {
        super(String.format("Not enough '%s' available in store. Need %.2f more.", stokaName, quantity));
    }
    
    public NotEnoughStokaAvailableException(String message) {
        super(message);
    }
}

