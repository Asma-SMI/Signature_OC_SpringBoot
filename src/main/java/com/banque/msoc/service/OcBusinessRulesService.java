package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcOperation;
import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.domain.enums.OcOperationStatus;
import com.banque.msoc.exception.InvalidDecisionException;
import com.banque.msoc.exception.InvalidStatusTransitionException;
import com.banque.msoc.exception.OperationAlreadyFinalizedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OcBusinessRulesService {

    public void validateDecisionInput(OcDecision decision, boolean finalize, String reason) {
        if (decision == null) throw new InvalidDecisionException("La décision est obligatoire.");
        if (decision == OcDecision.REJECT && !StringUtils.hasText(reason)) {
            throw new InvalidDecisionException("Le motif est obligatoire si la décision est REJECT.");
        }
        if (decision == OcDecision.PROGRESS && finalize) {
            throw new InvalidDecisionException("La décision PROGRESS ne peut pas être finale.");
        }
    }

    public void assertFlowModifiable(OcFlow flow) {
        if (flow.getStatus() == OcFlowStatus.ACCEPTED || flow.getStatus() == OcFlowStatus.REJECTED || flow.getStatus() == OcFlowStatus.RESPONSE_REQUESTED) {
            throw new InvalidStatusTransitionException("Le dossier est déjà finalisé et ne peut plus être modifié.");
        }
        if (flow.getStatus() == OcFlowStatus.SIGNATURE_INVALID) {
            throw new InvalidStatusTransitionException("Le dossier est bloqué car la signature entrante est invalide.");
        }
    }

    public void assertOperationModifiable(OcOperation operation) {
        if (operation.getStatus() == OcOperationStatus.VALIDATED) throw new OperationAlreadyFinalizedException();
        if (operation.getStatus() == OcOperationStatus.CANCELLED) throw new InvalidStatusTransitionException("L'opération est annulée.");
    }

    public void applyIntermediateDecision(OcOperation operation, OcDecision decision) {
        operation.setLastDecision(decision);
        operation.setStatus(OcOperationStatus.IN_PROGRESS);
        operation.setFinalized(false);
    }

    public void applyFinalDecision(OcFlow flow, OcOperation operation, OcDecision decision, String userId) {
        if (decision != OcDecision.ACCEPT && decision != OcDecision.REJECT) {
            throw new InvalidDecisionException("Seules les décisions ACCEPT ou REJECT peuvent finaliser un dossier.");
        }
        operation.setLastDecision(decision);
        operation.setStatus(OcOperationStatus.VALIDATED);
        operation.setFinalized(true);
        operation.setFinalizedBy(userId);
        operation.setFinalizedAt(java.time.LocalDateTime.now());
        flow.setFinalizedAt(java.time.LocalDateTime.now());
        flow.setStatus(decision == OcDecision.ACCEPT ? OcFlowStatus.ACCEPTED : OcFlowStatus.REJECTED);
    }
}
