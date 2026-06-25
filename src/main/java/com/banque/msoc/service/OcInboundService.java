package com.banque.msoc.service;

import com.banque.msoc.domain.entity.*;
import com.banque.msoc.domain.enums.*;
import com.banque.msoc.dto.kafka.OcInboundKafkaMessage;
import com.banque.msoc.dto.kafka.OcInboundPayloadDto;
import com.banque.msoc.dto.notification.OcNewFlowReceivedEvent;
import com.banque.msoc.exception.DuplicateMessageException;
import com.banque.msoc.exception.InvalidDecisionException;
import com.banque.msoc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OcInboundService {
    private final OcInboundEventRepository inboundEventRepository;
    private final OcFlowRepository flowRepository;
    private final OcOperationRepository operationRepository;
    private final OcPayloadService payloadService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processInboundMessage(OcInboundKafkaMessage message, String topic, Integer partition, Long offset) {
        if (message.getMessageId() == null || message.getPayload() == null || message.getPayload().getNumeroDossier() == null) {
            throw new InvalidDecisionException("Message Kafka entrant invalide: messageId, payload ou numeroDossier manquant.");
        }
        if (inboundEventRepository.existsByMessageId(message.getMessageId())) {
            throw new DuplicateMessageException(message.getMessageId());
        }

        OcInboundEvent inbound = inboundEventRepository.save(OcInboundEvent.builder()
                .messageId(message.getMessageId())
                .correlationId(message.getCorrelationId())
                .topic(topic)
                .partitionNo(partition)
                .offsetNo(offset)
                .status(EventStatus.PENDING)
                .build());

        OcInboundPayloadDto p = message.getPayload();
        OcFlowStatus status = message.getSignatureStatus() == SignatureStatus.INVALID
                ? OcFlowStatus.SIGNATURE_INVALID
                : OcFlowStatus.PENDING_REVIEW;

        OcFlow flow = OcFlow.builder()
                .businessKey(p.getNumeroDossier())
                .flowReference(p.getNumeroDemande())
                .flowType(message.getFlowType())
                .source(message.getSource())
                .signatureStatus(message.getSignatureStatus())
                .status(status)
                .correlationId(message.getCorrelationId())
                .inboundMessageId(message.getMessageId())
                .receivedAt(message.getReceivedAt())
                .build();
        flow.attachDetail(mapDetail(p));
        flow = flowRepository.save(flow);

        payloadService.savePayload(flow, PayloadType.INBOUND, message, "KAFKA");

        if (message.getSignatureStatus() == SignatureStatus.VALID) {
            operationRepository.save(OcOperation.builder()
                    .flow(flow)
                    .businessKey(flow.getBusinessKey())
                    .flowReference(flow.getFlowReference())
                    .flowType(flow.getFlowType())
                    .status(OcOperationStatus.PENDING_REVIEW)
                    .finalized(false)
                    .build());
        }

        inbound.setStatus(EventStatus.PROCESSED);
        inbound.setProcessedAt(LocalDateTime.now());
        inboundEventRepository.save(inbound);

        eventPublisher.publishEvent(new OcNewFlowReceivedEvent(
                flow.getBusinessKey(),
                p.getNumeroDossier(),
                p.getNumeroDemande(),
                flow.getStatus() != null ? flow.getStatus().name() : null,
                flow.getSignatureStatus() != null ? flow.getSignatureStatus().name() : null,
                flow.getReceivedAt() != null ? flow.getReceivedAt() : LocalDateTime.now(),
                message.getMessageId(),
                message.getCorrelationId()
        ));
    }

    private OcFlowDetail mapDetail(OcInboundPayloadDto p) {
        return OcFlowDetail.builder()
                .codTypMes(p.getTypeMessage())
                .codTypDoc(p.getTypeDocument())
                .numDemTtn(p.getNumeroDemande())
                .numDossTtn(p.getNumeroDossier())
                .numMessTtn(p.getNumeroMessage())
                .etat(p.getEtat())
                .emetteur(p.getEmetteur())
                .destinataire(p.getDestinataire())
                .codDouImp(p.getCodeDouaneImportateur())
                .raiSocImp(p.getRaisonSocialeImportateur())
                .adresseImp(p.getAdresseImportateur())
                .codTtnDec(p.getCodTtnDec())
                .nomSigDec(p.getNomSigDec())
                .datDec(p.getDatDec())
                .codBurDou(p.getCodeBureauDouane())
                .libBurDou(p.getLibelleBureauDouane())
                .codCpt(p.getCodCpt())
                .numRepDdm(p.getNumeroRepertoireDdm())
                .numDecDdm(p.getNumeroDeclarationDdm())
                .datDecDdm(p.getDateDeclarationDdm())
                .codBqImp(p.getCodeBanqueImportateur())
                .codOrgImp(p.getCodeOrganismeImportateur())
                .numRib(p.getRib())
                .numEnrOc(p.getNumeroEnregistrementOc())
                .datEnrOc(p.getDateEnregistrementOc())
                .montPrincipal(p.getMontantPrincipal())
                .montInteret(p.getMontantInteret())
                .montTot(p.getMontantTotal())
                .montLettre(p.getMontantLettre())
                .montRemise(p.getMontantRemise())
                .delaiPaie(p.getDelaiPaiement())
                .datEch(p.getDateEcheance())
                .codDecBq(p.getCodeDecisionBanque())
                .libDec(p.getLibelleDecision())
                .libCaution(p.getLibelleCaution())
                .nomOrgBq(p.getNomOrganismeBanque())
                .nomSigBq(p.getNomSignataireBanque())
                .datSigBq(p.getDateSignatureBanque())
                .motifRejet(p.getMotifRejet())
                .numQuittance(p.getNumeroQuittance())
                .datQuittance(p.getDateQuittance())
                .nomSigRec(p.getNomSignataireReception())
                .datSigRec(p.getDateSignatureReception())
                .idSeq(p.getIdSeq())
                .indTransact(p.getIndicateurTransaction())
                .libBqImp(p.getLibelleBanqueImportateur())
                .libOrgImp(p.getLibelleOrganismeImportateur())
                .motifAnnul(p.getMotifAnnulation())
                .build();
    }
}
