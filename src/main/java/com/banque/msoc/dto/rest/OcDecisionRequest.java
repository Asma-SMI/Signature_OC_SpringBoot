package com.banque.msoc.dto.rest;

import com.banque.msoc.domain.enums.OcDecision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OcDecisionRequest {
    @NotNull(message = "La décision est obligatoire")
    private OcDecision decision;

    @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères")
    private String reason;

    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
    private String comment;
}
