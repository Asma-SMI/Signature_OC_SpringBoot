package com.banque.msoc.exception;

public class InvalidStatusTransitionException extends MsOcException {
    public InvalidStatusTransitionException(String message) { super("OC_ERR_003", message); }
}
