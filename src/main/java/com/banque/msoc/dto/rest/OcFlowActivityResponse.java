package com.banque.msoc.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcFlowActivityResponse {
    private String dateKey;

    private String jour;

    private long flux;
}
