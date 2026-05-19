package com.banque.msoc.dto.rest;

import com.banque.msoc.domain.enums.OcFlowStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OcFlowSearchCriteria {
    private OcFlowStatus status;
    private String flowReference;
    private String flowType;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
