package com.banque.msoc.dto.rest;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class OcFlowDetailResponse {
    private String codTypMes;
    private String codTypDoc;
    private String etat;

    private String numMessTtn;
    private String numDossTtn;
    private String numDemTtn;

    private String emetteur;
    private String destinataire;

    private String codDouImp;
    private String raiSocImp;
    private String adresseImp;

    private String codTtnDec;
    private String nomSigDec;
    private LocalDate datDec;

    private String codBurDou;
    private String libBurDou;
    private String codCpt;

    private String numRepDdm;

    private String numDecDdm;
    private LocalDate datDecDdm;

    private String codBqImp;
    private String libBqImp;
    private String codOrgImp;
    private String libOrgImp;
    private String numRib;

    private String numEnrOc;
    private LocalDate datEnrOc;

    private BigDecimal montPrincipal;
    private BigDecimal montInteret;
    private BigDecimal montTot;
    private String montLettre;
    private BigDecimal montRemise;
    private String delaiPaie;
    private LocalDate datEch;

    private String codDecBq;
    private String libDec;
    private String libCaution;
    private String nomOrgBq;
    private String nomSigBq;
    private LocalDate datSigBq;
    private String motifRejet;

    private String numQuittance;
    private LocalDate datQuittance;

    private String nomSigRec;
    private LocalDate datSigRec;

    private String idSeq;
    private String indTransact;
    private String motifAnnul;
}
