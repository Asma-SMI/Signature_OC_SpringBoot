package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcFlowDetail;
import com.banque.msoc.domain.entity.OcOutboundEvent;
import com.banque.msoc.domain.enums.EventStatus;
import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.domain.enums.PayloadType;
import com.banque.msoc.dto.kafka.OcOutboundKafkaMessage;
import com.banque.msoc.dto.rest.OcDecisionRequest;
import com.banque.msoc.repository.OcOutboundEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OcOutboundEventService {
    private final OcOutboundEventRepository repository;
    private final OcPayloadService payloadService;
    private final ObjectMapper objectMapper;

    @Value("${msoc.kafka.outbound-topic}")
    private String outboundTopic;

    @Transactional
    public OcOutboundEvent createPendingOutboundEvent(OcFlow flow, OcDecisionRequest request, String decisionUser) {
        OcOutboundKafkaMessage message = OcOutboundKafkaMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .correlationId(flow.getCorrelationId())
                .businessKey(flow.getBusinessKey())
                .flowType(flow.getFlowType())
                .decision(request.getDecision())
                .dossierStatus(flow.getStatus())
                .requestedAction("GENERATE_AND_SIGN")
                .timestamp(LocalDateTime.now())
                .responsePayload(buildResponsePayload(flow, request, decisionUser))
                .build();
        try {
            String json = objectMapper.writeValueAsString(message);
            OcOutboundEvent event = repository.save(OcOutboundEvent.builder()
                    .flow(flow)
                    .messageId(message.getMessageId())
                    .topic(outboundTopic)
                    .status(EventStatus.PENDING)
                    .payloadJson(json)
                    .build());
            payloadService.savePayload(flow, PayloadType.OUTBOUND_REQUEST, message, decisionUser);
            return event;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de créer le message sortant", e);
        }
    }

    private Map<String, Object> buildResponsePayload(OcFlow flow, OcDecisionRequest request, String user) {
        Map<String, Object> payload = new LinkedHashMap<>();

        OcFlowDetail detail = flow.getDetail();

        boolean accepted = request.getDecision() == OcDecision.ACCEPT;
        String outboundTypeDocument = accepted ? "O04" : "O03";
        String decisionCode = accepted ? "ACCEPT" : "REJECT";
        String decisionLabel = accepted ? "Acceptation bancaire" : "Rejet bancaire";

        payload.put("typeMessage", detail.getCodTypMes());
        payload.put("typeDocument", outboundTypeDocument);
        payload.put("etat", detail.getEtat());

        payload.put("numeroDemande", detail.getNumDemTtn());
        payload.put("numeroDossier", detail.getNumDossTtn());
        payload.put("numeroMessage", detail.getNumMessTtn());

        payload.put("emetteur", detail.getEmetteur());
        payload.put("destinataire", detail.getDestinataire());

        payload.put("codeDouaneImportateur", detail.getCodDouImp());
        payload.put("raisonSocialeImportateur", detail.getRaiSocImp());
        payload.put("adresseImportateur", detail.getAdresseImp());

        payload.put("codeTtnDeclaration", detail.getCodTtnDec());
        payload.put("nomSignataireDeclaration", detail.getNomSigDec());
        payload.put("dateDeclaration", detail.getDatDec());

        payload.put("codeBureauDouane", detail.getCodBurDou());
        payload.put("libelleBureauDouane", detail.getLibBurDou());
        payload.put("codCpt", detail.getCodCpt());

        payload.put("numeroRepertoireDdm", detail.getNumRepDdm());

        payload.put("numeroDeclarationDdm", detail.getNumDecDdm());
        payload.put("dateDeclarationDdm", detail.getDatDecDdm());

        payload.put("codeBanqueImportateur", detail.getCodBqImp());
        payload.put("libelleBanqueImportateur", detail.getLibBqImp());
        payload.put("codeOrganismeImportateur", detail.getCodOrgImp());
        payload.put("libelleOrganismeImportateur", detail.getLibOrgImp());
        payload.put("rib", detail.getNumRib());

        payload.put("numeroEnregistrementOc", detail.getNumEnrOc());
        payload.put("dateEnregistrementOc", detail.getDatEnrOc());

        payload.put("montantPrincipal", detail.getMontPrincipal());
        payload.put("montantInteret", detail.getMontInteret());
        payload.put("montantTotal", detail.getMontTot());
        payload.put("montantLettre", detail.getMontLettre());
        payload.put("montantRemise", detail.getMontRemise());
        payload.put("delaiPaiement", detail.getDelaiPaie());
        payload.put("dateEcheance", detail.getDatEch());

        payload.put("codeDecisionBanque", decisionCode);
        payload.put("libelleDecision", decisionLabel);
        payload.put("libelleCaution", detail.getLibCaution());
        payload.put("nomOrganismeBanque", detail.getNomOrgBq());
        payload.put("nomSignataireBanque", detail.getNomSigBq());
        payload.put("dateSignatureBanque", detail.getDatSigBq());
        payload.put("motifRejet", accepted ? null : request.getReason());

        payload.put("numeroQuittance", detail.getNumQuittance());
        payload.put("dateQuittance", detail.getDatQuittance());

        payload.put("nomSignataireReception", detail.getNomSigRec());
        payload.put("dateSignatureReception", detail.getDatSigRec());

        payload.put("idSeq", detail.getIdSeq());
        payload.put("indicateurTransaction", detail.getIndTransact());

        payload.put("motifAnnulation", detail.getMotifAnnul());

        payload.put("decisionCode", decisionCode);
        payload.put("decisionDate", LocalDateTime.now().toString());
        payload.put("decisionUser", user);
        payload.put("reason", request.getReason());
        payload.put("comment", request.getComment());

        return payload;
    }
}
