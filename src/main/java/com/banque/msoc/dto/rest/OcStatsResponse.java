package com.banque.msoc.dto.rest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcStatsResponse {
    private long totalFlux;
    private long fluxErreurOutbound;
    private long fluxEnAttente;

    private long fluxAcceptes;

    private long fluxRejetes;
}
