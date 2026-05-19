package com.banque.msoc.exception;

public class OperationAlreadyFinalizedException extends MsOcException {
    public OperationAlreadyFinalizedException() { super("OC_ERR_010", "L'opération est déjà finalisée et ne peut plus être modifiée."); }
}
