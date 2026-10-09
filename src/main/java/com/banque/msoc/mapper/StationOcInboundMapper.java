package com.banque.msoc.mapper;

import com.banque.msoc.dto.kafka.OcInboundKafkaMessage;
import com.banque.msoc.dto.kafka.OcInboundPayloadDto;
import com.banque.msoc.dto.kafka.station.StationOcInboundDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StationOcInboundMapper {

    private final ObjectMapper objectMapper;

    public OcInboundKafkaMessage toLegacyMessage(
            StationOcInboundDto station) {

        OcInboundKafkaMessage target =
                new OcInboundKafkaMessage();

        if (station.getHeader() != null) {
            target.setMessageId(
                    station.getHeader().getMessageId()
            );
            target.setCorrelationId(
                    station.getHeader().getCorrelationId()
            );
            target.setReceivedAt(
                    station.getHeader().getReceivedAt()
            );
        }

        target.setVersion(
                station.getHeader().getVersion()
        );

        target.setPartner(
                station.getHeader().getPartner()
        );

        target.setSource("EXCHANGE_STATION");

        if (station.getResult() != null) {
            target.setSignatureStatus(
                    station.getResult().getSignatureStatus()
            );
        }

        OcInboundPayloadDto payload =
                new OcInboundPayloadDto();

        if (station.getResult() != null) {
            payload.setTypeMessage(
                    station.getResult().getTypeMessage()
            );
            payload.setTypeDocument(
                    station.getResult().getTypeDocument()
            );
        }

        JsonNode data = station.getData() == null
                ? objectMapper.createObjectNode()
                : objectMapper.valueToTree(station.getData());

        // DOCUMENT
        payload.setEtat(
                text(data, "document", "etat")
        );

        // REFERENCES
        payload.setNumeroDemande(
                text(data, "references", "numeroDemande")
        );
        payload.setNumeroDossier(
                text(data, "references", "numeroDossier")
        );
        payload.setNumeroMessage(
                text(data, "references", "numeroMessage")
        );
        payload.setUtilisateur(
                text(data, "references", "utilisateur")
        );

        // ROUTAGE
        payload.setEmetteur(
                text(data, "routage", "emetteur")
        );
        payload.setDestinataire(
                text(data, "routage", "destinataire")
        );

        // IMPORTATEUR
        payload.setCodeDouaneImportateur(
                text(data, "parties", "importateur", "code")
        );
        payload.setRaisonSocialeImportateur(
                text(data, "parties", "importateur", "raisonSociale")
        );
        payload.setAdresseImportateur(
                text(data, "parties", "importateur", "adresse", "ligne1")
        );

        // DECLARANT
        payload.setCodTtnDec(
                text(data, "parties", "declarant", "codeTtn")
        );
        payload.setNomSigDec(
                text(data, "parties", "declarant", "nomSignataire")
        );
        payload.setDatDec(
                date(data, "parties", "declarant", "date")
        );

        // BUREAU DOUANE
        payload.setCodeBureauDouane(
                text(data, "douane", "bureau", "code")
        );
        payload.setLibelleBureauDouane(
                text(data, "douane", "bureau", "libelle")
        );
        payload.setCodCpt(
                text(data, "douane", "bureau", "codeComptable")
        );

        // DDM
        payload.setNumeroRepertoireDdm(
                text(data, "douane", "ddm", "numeroRepertoire")
        );
        payload.setNumeroDeclarationDdm(
                text(data, "douane", "ddm", "numeroDeclaration")
        );
        payload.setDateDeclarationDdm(
                date(data, "douane", "ddm", "dateDeclaration")
        );

        // BANQUE IMPORTATEUR
        payload.setCodeBanqueImportateur(
                text(data, "parties", "banqueImportateur", "banque", "code")
        );
        payload.setLibelleBanqueImportateur(
                text(data, "parties", "banqueImportateur", "banque", "libelle")
        );
        payload.setCodeOrganismeImportateur(
                text(data, "parties", "banqueImportateur", "agence", "code")
        );
        payload.setLibelleOrganismeImportateur(
                text(data, "parties", "banqueImportateur", "agence", "libelle")
        );
        payload.setRib(
                text(data, "parties", "banqueImportateur", "rib")
        );

        // OBLIGATION CAUTIONNEE
        payload.setNumeroEnregistrementOc(
                text(data, "obligationCautionnee", "numero")
        );
        payload.setDateEnregistrementOc(
                date(data, "obligationCautionnee", "date")
        );

        // PAIEMENT
        payload.setMontantPrincipal(
                decimal(data, "paiement", "montantPrincipal")
        );
        payload.setMontantInteret(
                decimal(data, "paiement", "montantInteret")
        );
        payload.setMontantTotal(
                decimal(data, "paiement", "montantTotal")
        );
        payload.setMontantLettre(
                text(data, "paiement", "montantTotalLettre")
        );
        payload.setMontantRemise(
                decimal(data, "paiement", "montantRemise")
        );
        payload.setDelaiPaiement(
                text(data, "paiement", "delaiPaiement")
        );
        payload.setDateEcheance(
                date(data, "paiement", "dateEcheance")
        );

        // DECISION BANQUE
        payload.setCodeDecisionBanque(
                text(data, "decisionBanque", "decision", "code")
        );
        payload.setLibelleDecision(
                text(data, "decisionBanque", "decision", "libelle")
        );
        payload.setLibelleCaution(
                text(data, "decisionBanque", "caution")
        );
        payload.setMotifRejet(
                text(data, "decisionBanque", "motifRejet")
        );

        // SIGNATURE BANQUE
        payload.setNomOrganismeBanque(
                text(data, "signataires", "banque", "nomOrganisme")
        );
        payload.setNomSignataireBanque(
                text(data, "signataires", "banque", "nomSignataire")
        );
        payload.setDateSignatureBanque(
                date(data, "signataires", "banque", "date")
        );

        // QUITTANCE
        payload.setNumeroQuittance(
                text(data, "quittance", "numero")
        );
        payload.setDateQuittance(
                date(data, "quittance", "date")
        );

        // PAS DE CORRESPONDANCE EXPLICITE
        payload.setNomSignataireReception(
                text(data, "signataires", "receveurDouane", "nomSignataire")
        );
        payload.setDateSignatureReception(
                date(data, "signataires", "receveurDouane", "date")
        );

        // TECHNIQUE
        payload.setIdSeq(
                text(data, "technique", "idSeq")
        );
        payload.setIndicateurTransaction(
                text(data, "technique", "indicateurTransaction")
        );

        // ANNULATION
        payload.setMotifAnnulation(
                text(data, "annulation", "motif")
        );

        target.setPayload(payload);
        return target;
    }

    // =========================
    // HELPERS NULL-SAFE
    // =========================

    private JsonNode node(
            JsonNode root,
            String... path) {

        JsonNode current = root;

        for (String field : path) {
            if (current == null ||
                    current.isNull() ||
                    current.isMissingNode()) {
                return null;
            }
            current = current.path(field);
        }

        return current == null ||
                current.isNull() ||
                current.isMissingNode()
                ? null
                : current;
    }

    private String text(
            JsonNode root,
            String... path) {

        JsonNode value = node(root, path);
        return value == null ? null : value.asText();
    }

    private BigDecimal decimal(
            JsonNode root,
            String... path) {

        String value = text(root, path);

        if (value == null || value.isBlank()) {
            return null;
        }

        return new BigDecimal(value);
    }

    private LocalDate date(
            JsonNode root,
            String... path) {

        String value = text(root, path);

        if (value == null || value.isBlank()) {
            return null;
        }

        // Accepte yyyy-MM-dd et yyyy-MM-ddTHH:mm:ss
        if (value.length() > 10 &&
                value.charAt(10) == 'T') {
            return LocalDateTime.parse(value).toLocalDate();
        }

        return LocalDate.parse(value);
    }
}