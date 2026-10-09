package com.banque.msoc.dto.kafka.station;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StationOcOutboundDto {
    private Header header;

    private StationOcInboundDto.FluxData data;

    @Data
    public static class Header {

        private String version;

        private String requestId;

        private String correlationId;

        private String partner;

        private String processId;

        private String decision;

        private LocalDateTime requestedAt;
    }
}
