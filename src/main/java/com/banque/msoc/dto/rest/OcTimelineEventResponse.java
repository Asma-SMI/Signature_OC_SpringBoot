package com.banque.msoc.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcTimelineEventResponse {
    private LocalDateTime timestamp;

    private String type;

    private String description;

    private String utilisateur;
}
