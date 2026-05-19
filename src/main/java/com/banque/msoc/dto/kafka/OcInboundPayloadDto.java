package com.banque.msoc.dto.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcInboundPayloadDto {

    private String typeMessage;
    private String typeDocument;
    private String etat;

    private String numeroDemande;
    private String numeroDossier;
    private String numeroMessage;

    private String emetteur;
    private String destinataire;

    private String codeDouaneImportateur;
    private String raisonSocialeImportateur;
    private String adresseImportateur;

    private String codeTtnDeclaration;
    private String nomSignataireDeclaration;
    private LocalDate dateDeclaration;

    private String codeBureauDouane;
    private String libelleBureauDouane;
    private String codCpt;

    private String numeroRepertoireDdm;

    private String numeroDeclarationDdm;
    private LocalDate dateDeclarationDdm;

    private String codeBanqueImportateur;
    private String libelleBanqueImportateur;
    private String codeOrganismeImportateur;
    private String libelleOrganismeImportateur;
    private String rib;

    private String numeroEnregistrementOc;
    private LocalDate dateEnregistrementOc;

    private BigDecimal montantPrincipal;
    private BigDecimal montantInteret;
    private BigDecimal montantTotal;
    private String montantLettre;
    private BigDecimal montantRemise;
    private String delaiPaiement;
    private LocalDate dateEcheance;

    private String codeDecisionBanque;
    private String libelleDecision;
    private String libelleCaution;
    private String nomOrganismeBanque;
    private String nomSignataireBanque;
    private LocalDate dateSignatureBanque;
    private String motifRejet;

    private String numeroQuittance;
    private LocalDate dateQuittance;

    private String nomSignataireReception;
    private LocalDate dateSignatureReception;

    private String idSeq;
    private String indicateurTransaction;

    private String motifAnnulation;
}