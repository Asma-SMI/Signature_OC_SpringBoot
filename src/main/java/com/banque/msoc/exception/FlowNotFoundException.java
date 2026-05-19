package com.banque.msoc.exception;

public class FlowNotFoundException extends MsOcException {
    public FlowNotFoundException(String businessKey) {
        super("OC_ERR_001", "Le flux spécifié n'a pas été trouvé: " + businessKey);
    }
}
