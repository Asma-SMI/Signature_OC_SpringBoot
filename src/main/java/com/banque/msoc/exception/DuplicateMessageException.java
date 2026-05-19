package com.banque.msoc.exception;

public class DuplicateMessageException extends MsOcException {
    public DuplicateMessageException(String messageId) { super("OC_ERR_004", "Message dupliqué: " + messageId); }
}
