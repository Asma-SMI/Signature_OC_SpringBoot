package com.banque.msoc.exception;

public class InvalidDecisionException extends MsOcException {
    public InvalidDecisionException(String message) { super("OC_ERR_002", message); }
}
