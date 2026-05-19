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
    private String numMessTtn;
    private String numDossTtn;
    private String numDemTtn;
    private String emetteur;
    private String destinataire;
    private String numRib;
    private BigDecimal montPrincipal;
    private BigDecimal montInteret;
    private BigDecimal montTot;
    private LocalDate datEch;
    private String motifRejet;
}
