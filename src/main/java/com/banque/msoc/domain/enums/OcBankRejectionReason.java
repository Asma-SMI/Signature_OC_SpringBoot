package com.banque.msoc.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OcBankRejectionReason {
    DONNEES_INCORRECTES(
            "MOTIF_01",
            "Données incorrectes"
    ),

    DOSSIER_NON_CONFORME(
            "MOTIF_02",
            "Dossier non conforme"
    );

    private final String code;
    private final String libelle;
}
