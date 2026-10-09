package com.banque.msoc.mapper;

import com.banque.msoc.domain.enums.OcBankDecisionCode;
import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.dto.kafka.OcInboundPayloadDto;
import com.banque.msoc.dto.kafka.station.StationOcInboundDto;
import com.banque.msoc.dto.kafka.station.StationOcOutboundDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StationOcOutboundMapper {
    private final ObjectMapper objectMapper;

    public StationOcOutboundDto map(
            OcInboundPayloadDto p,
            String correlationId,
            String version,
            String partner,
            OcDecision decision
    ) {
        StationOcOutboundDto result =
                new StationOcOutboundDto();

        // HEADER
        StationOcOutboundDto.Header header =
                new StationOcOutboundDto.Header();

        header.setVersion(version);
        header.setRequestId(UUID.randomUUID().toString());
        header.setCorrelationId(correlationId);
        header.setPartner(partner);
        header.setProcessId("OBLIG_CAUT");
        header.setDecision(
                decision != null ? decision.name() : null
        );
        header.setRequestedAt(LocalDateTime.now());

        result.setHeader(header);

        // Structure data complete
        ObjectNode data = createEmptyData();

        if (p != null) {

            // DOCUMENT
            set(data, "document.etat", p.getEtat());

            // REFERENCES
            set(data, "references.numeroDemande",
                    p.getNumeroDemande());
            set(data, "references.numeroDossier",
                    p.getNumeroDossier());
            set(data, "references.numeroMessage",
                    p.getNumeroMessage());
            set(data, "references.utilisateur",
                    p.getUtilisateur());

            // ROUTAGE
            set(data, "routage.emetteur",
                    p.getEmetteur());
            set(data, "routage.destinataire",
                    p.getDestinataire());

            // IMPORTATEUR
            set(data, "parties.importateur.code",
                    p.getCodeDouaneImportateur());
            set(data, "parties.importateur.raisonSociale",
                    p.getRaisonSocialeImportateur());
            set(data, "parties.importateur.adresse.ligne1",
                    p.getAdresseImportateur());

            // DECLARANT
            set(data, "parties.declarant.codeTtn",
                    p.getCodTtnDec());
            set(data, "parties.declarant.nomSignataire",
                    p.getNomSigDec());
            set(data, "parties.declarant.date",
                    p.getDatDec());

            // BANQUE IMPORTATEUR
            set(data, "parties.banqueImportateur.banque.code",
                    p.getCodeBanqueImportateur());
            set(data, "parties.banqueImportateur.banque.libelle",
                    p.getLibelleBanqueImportateur());
            set(data, "parties.banqueImportateur.agence.code",
                    p.getCodeOrganismeImportateur());
            set(data, "parties.banqueImportateur.agence.libelle",
                    p.getLibelleOrganismeImportateur());
            set(data, "parties.banqueImportateur.rib",
                    p.getRib());

            // SIGNATURE BANQUE
            set(data, "signataires.banque.nomOrganisme",
                    p.getNomOrganismeBanque());
            set(data, "signataires.banque.nomSignataire",
                    p.getNomSignataireBanque());
            set(data, "signataires.banque.date",
                    p.getDateSignatureBanque());

            // BUREAU DOUANE
            set(data, "douane.bureau.code",
                    p.getCodeBureauDouane());
            set(data, "douane.bureau.libelle",
                    p.getLibelleBureauDouane());
            set(data, "douane.bureau.codeComptable",
                    p.getCodCpt());

            // DDM
            set(data, "douane.ddm.numeroRepertoire",
                    p.getNumeroRepertoireDdm());
            set(data, "douane.ddm.numeroDeclaration",
                    p.getNumeroDeclarationDdm());
            set(data, "douane.ddm.dateDeclaration",
                    p.getDateDeclarationDdm());

            // OBLIGATION CAUTIONNEE
            set(data, "obligationCautionnee.numero",
                    p.getNumeroEnregistrementOc());

            if (p.getDateEnregistrementOc() != null) {
                set(data, "obligationCautionnee.date",
                        p.getDateEnregistrementOc()
                                .atStartOfDay());
            }

            // PAIEMENT
            set(data, "paiement.montantPrincipal",
                    p.getMontantPrincipal());
            set(data, "paiement.montantInteret",
                    p.getMontantInteret());
            set(data, "paiement.montantTotal",
                    p.getMontantTotal());
            set(data, "paiement.montantTotalLettre",
                    p.getMontantLettre());
            set(data, "paiement.montantRemise",
                    p.getMontantRemise());

            if (p.getDelaiPaiement() != null &&
                    !p.getDelaiPaiement().isBlank()) {
                set(data, "paiement.delaiPaiement",
                        Integer.valueOf(p.getDelaiPaiement()));
            }

            set(data, "paiement.dateEcheance",
                    p.getDateEcheance());

            // DECISION BANQUE
            OcBankDecisionCode bankDecision =
                    OcBankDecisionCode.fromDecision(decision);

                // Code bancaire
            set(data, "decisionBanque.decision.code",
                    bankDecision.getCode());

                // Libelle bancaire
            set(data, "decisionBanque.decision.libelle",
                    bankDecision.getLibelle());

            if (decision == OcDecision.ACCEPT) {

                // Caution bancaire uniquement si acceptation
                String montantLettre = p.getMontantLettre();

                String caution = montantLettre == null
                        || montantLettre.isBlank()
                        ? null
                        : "Lu et Approuve. Bon pour " + montantLettre;

                set(data, "decisionBanque.caution", caution);

                // Motif de rejet = null

            } else if (decision == OcDecision.REJECT) {

                // Pas de caution en cas de rejet

                set(data, "decisionBanque.motifRejet",
                        p.getMotifRejet());
            }

            // QUITTANCE
            set(data, "quittance.numero",
                    p.getNumeroQuittance());
            set(data, "quittance.date",
                    p.getDateQuittance());

            // ANNULATION
            set(data, "annulation.motif",
                    p.getMotifAnnulation());

            // TECHNIQUE
            set(data, "technique.idSeq",
                    p.getIdSeq());
            set(data, "technique.indicateurTransaction", "S");
        }

        result.setData(objectMapper.convertValue(
                data,
                StationOcInboundDto.FluxData.class
        ));

        return result;
    }

    private ObjectNode createEmptyData() {

        ObjectNode root = objectMapper.createObjectNode();

        String[] paths = {
                "document",
                "references",
                "routage",
                "authentification",
                "messageOrigine",

                "parties",
                "parties.importateur",
                "parties.importateur.adresse",
                "parties.exportateur",
                "parties.exportateur.adresse",
                "parties.declarant",
                "parties.banqueImportateur",
                "parties.banqueImportateur.banque",
                "parties.banqueImportateur.agence",

                "signataires",
                "signataires.banque",
                "signataires.receveurDouane",
                "signataires.banqueCentrale",
                "signataires.ministereCommerce",
                "signataires.organismeDomiciliataire",
                "signataires.organismeTechnique",

                "douane",
                "douane.bureau",
                "douane.ddm",
                "douane.enregistrement",
                "douane.imputation",
                "douane.imputation.bureau",
                "douane.statutDocument",
                "douane.statutDocument.statut",
                "douane.affectation",

                "commerce",
                "commerce.natureAutorisation",
                "commerce.regimeStatistique",
                "commerce.modeLivraison",
                "commerce.modeLivraison.incoterme",
                "commerce.pays",
                "commerce.pays.provenance",
                "commerce.pays.achat",
                "commerce.pays.premiereDestination",
                "commerce.pays.destinationDefinitive",
                "commerce.pays.origine",
                "commerce.contratCommercial",
                "commerce.validite",
                "commerce.depot",
                "commerce.depot.organismeDepositaire",
                "commerce.domiciliation",
                "commerce.domiciliation.organisme",
                "commerce.reglementFinancier",
                "commerce.reglementFinancier.mode",
                "commerce.reglementFinancier.devise",

                "montants",
                "montants.devise",
                "montants.devise.devise",
                "montants.dinars",

                "obligationCautionnee",
                "paiement",
                "decisionBanque",
                "decisionBanque.decision",
                "quittance",
                "annulation",

                "statuts",
                "statuts.banque",
                "statuts.ministereCommerce",
                "statuts.organismeTechnique",

                "observations",
                "technique"
        };

        for (String path : paths) {
            ensureObject(root, path);
        }

        // Tableaux vides par defaut
        emptyArray(root,
                "douane.statutDocument.etatsDeclaration");
        emptyArray(root,
                "douane.statutDocument.messagesInspecteur");

        emptyArray(root, "observations.generales");
        emptyArray(root, "observations.banque");
        emptyArray(root, "observations.ministereCommerce");
        emptyArray(root, "observations.organismeTechnique");

        emptyArray(root, "articles");
        emptyArray(root, "piecesJointes");
        emptyArray(root, "erreurs");

        // Decision douaniere : conserver un objet vide
        ObjectNode decision = objectMapper.createObjectNode();
        decision.putNull("decision");
        decision.putNull("reserve");
        decision.putNull("rdvVisite");
        decision.putNull("date");
        decision.putNull("montantPenalites");

        ArrayNode decisions = objectMapper.createArrayNode();
        decisions.add(decision);
        ensureObject(root, "douane").set("decisions", decisions);

        return root;
    }

    private ObjectNode ensureObject(
            ObjectNode root,
            String path
    ) {
        String[] names = path.split("\\.");
        ObjectNode current = root;

        for (String name : names) {
            if (!current.has(name) ||
                    !current.get(name).isObject()) {
                current.set(name,
                        objectMapper.createObjectNode());
            }
            current = (ObjectNode) current.get(name);
        }

        return current;
    }

    private void emptyArray(
            ObjectNode root,
            String path
    ) {
        int index = path.lastIndexOf('.');
        String parent = index < 0
                ? ""
                : path.substring(0, index);

        String field = path.substring(index + 1);

        ObjectNode node = parent.isEmpty()
                ? root
                : ensureObject(root, parent);

        node.set(field, objectMapper.createArrayNode());
    }


    private void set(
            ObjectNode root,
            String path,
            Object value
    ) {
        if (value == null) {
            return;
        }

        int index = path.lastIndexOf('.');
        String parent = index < 0
                ? ""
                : path.substring(0, index);
        String field = path.substring(index + 1);

        ObjectNode target = parent.isEmpty()
                ? root
                : ensureObject(root, parent);

        target.set(field,
                objectMapper.valueToTree(value));
    }
}
