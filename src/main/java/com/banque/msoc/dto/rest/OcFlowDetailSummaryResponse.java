package com.banque.msoc.dto.rest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcFlowDetailSummaryResponse {
    private String codTypMes;
    private String codTypDoc;
    private String etat;

    private String numMessTtn;
    private String numDossTtn;
    private String numDemTtn;
}
