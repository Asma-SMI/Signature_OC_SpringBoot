package com.banque.msoc.dto.rest;

import com.banque.msoc.dto.kafka.OcOutboundEventResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcOutboundDashboardResponse {
    private long enAttenteEnvoi;

    private long envoyes;

    private long enErreur;

    private List<OcOutboundEventResponse> derniersFluxSortants;
}
