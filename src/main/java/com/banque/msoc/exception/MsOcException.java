package com.banque.msoc.exception;

public abstract class MsOcException extends RuntimeException {
    private final String errorCode;
    protected MsOcException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public String getErrorCode() { return errorCode; }
}
