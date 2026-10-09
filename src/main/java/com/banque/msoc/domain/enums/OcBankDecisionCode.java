package com.banque.msoc.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OcBankDecisionCode {
    FAVORABLE("39", "Favorable"),

    DEFAVORABLE("REJECT_TEMP", "Défavorable");

    private final String code;
    private final String libelle;

    public static OcBankDecisionCode fromDecision(
            OcDecision decision) {

        return decision == OcDecision.ACCEPT
                ? FAVORABLE
                : DEFAVORABLE;
    }
}
