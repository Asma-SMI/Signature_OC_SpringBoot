package com.banque.msoc.dto.kafka.station;

import com.banque.msoc.domain.enums.SignatureStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class StationOcInboundDto {
    private Header header;
    private Result result;
    private FluxData data;

    // HEADER
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String version;
        private String messageId;
        private String correlationId;
        private String partner;
        private LocalDateTime receivedAt;
    }

    // RESULT
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private Long enteteFluxId;
        private String typeMessage;
        private String typeDocument;
        private Boolean valid;
        private SignatureStatus signatureStatus;
        private List<ResultError> errors;
        private Boolean fullyMapped;
        private List<UnmappedField> unmappedFields;
        private String aperakSent;
        private String duplicateOf;
    }

    @Data
    public static class ResultError {
        private String code;
        private String libelle;
        private String field;
        private Integer occurrence;
    }

    @Data
    public static class UnmappedField {
        private String field;
        private String ttnCode;
    }

    // DATA
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FluxData {
        private Document document;
        private References references;
        private Routage routage;
        private Authentification authentification;
        private MessageOrigine messageOrigine;
        private Parties parties;
        private Signataires signataires;
        private Douane douane;
        private Commerce commerce;
        private Montants montants;
        private ObligationCautionnee obligationCautionnee;
        private Paiement paiement;
        private DecisionBanque decisionBanque;
        private Quittance quittance;
        private Annulation annulation;
        private Statuts statuts;
        private Observations observations;
        private List<Article> articles;
        private List<PieceJointe> piecesJointes;
        private List<Erreur> erreurs;
        private Technique technique;
    }

    // TYPES REUTILISABLES
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CodeLibelle {
        private String code;
        private String libelle;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Adresse {
        private String ligne1;
        private String ligne2;
        private String ligne3;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Signature {
        private String nomOrganisme;
        private String nomSignataire;
        private LocalDate date;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NumeroDate {
        private String numero;
        private LocalDateTime date;
    }

    // DOCUMENT
    @Data
    public static class Document {
        private String etat;
        private LocalDate dateEmission;
    }

    // REFERENCES
    @Data
    public static class References {
        private String numeroDemande;
        private String numeroDossier;
        private String numeroMessage;
        private String utilisateur;
    }

    // ROUTAGE
    @Data
    public static class Routage {
        private String emetteur;
        private String destinataire;
    }

    // AUTHENTIFICATION
    @Data
    public static class Authentification {
        private String cleAuth;
    }

    // MESSAGE ORIGINE
    @Data
    public static class MessageOrigine {
        private String nomCode;
        private String numero;
        private LocalDate dateEmission;
    }

    // PARTIES
    @Data
    public static class Parties {
        private Personne importateur;
        private Personne exportateur;
        private Declarant declarant;
        private BanqueImportateur banqueImportateur;
    }

    @Data
    public static class Personne {
        private String code;
        private String raisonSociale;
        private String nomPrenom;
        private LocalDate dateNaissance;
        private Adresse adresse;
    }

    @Data
    public static class Declarant {
        private String codeTtn;
        private String nomOrganisme;
        private String nomSignataire;
        private LocalDate date;
    }

    @Data
    public static class BanqueImportateur {
        private CodeLibelle banque;
        private CodeLibelle agence;
        private String rib;
    }

    // SIGNATAIRES
    @Data
    public static class Signataires {
        private Signature banque;
        private Signature receveurDouane;
        private Signature banqueCentrale;
        private Signature ministereCommerce;
        private Signature organismeDomiciliataire;
        private Signature organismeTechnique;
    }

    // DOUANE
    @Data
    public static class Douane {
        private Bureau bureau;
        private Ddm ddm;
        private NumeroDate enregistrement;
        private Imputation imputation;
        private StatutDocument statutDocument;
        private Affectation affectation;
        private String motPasse;
        private BigDecimal valeurDouaneTotaleDinars;
        private BigDecimal consignationPenalites;
        private List<DecisionDouane> decisions;
    }

    @Data
    public static class Bureau {
        private String code;
        private String libelle;
        private String codeComptable;
    }

    @Data
    public static class Ddm {
        private String numeroRepertoire;
        private String numeroDeclaration;
        private LocalDate dateDeclaration;
    }

    @Data
    public static class Imputation {
        private String numeroDeclaration;
        private LocalDate date;
        private CodeLibelle bureau;
    }

    @Data
    public static class StatutDocument {
        private CodeLibelle statut;
        private List<JsonNode> etatsDeclaration;
        private List<JsonNode> messagesInspecteur;
    }

    @Data
    public static class Affectation {
        private String nomInspecteur;
        private LocalDate date;
    }

    @Data
    public static class DecisionDouane {
        private String decision;
        private String reserve;
        private String rdvVisite;
        private LocalDate date;
        private BigDecimal montantPenalites;
    }

    // COMMERCE
    @Data
    public static class Commerce {
        private CodeLibelle natureAutorisation;
        private CodeLibelle regimeStatistique;
        private ModeLivraison modeLivraison;
        private PaysCommerce pays;
        private NumeroDate contratCommercial;
        private Validite validite;
        private Depot depot;
        private Domiciliation domiciliation;
        private ReglementFinancier reglementFinancier;
    }

    @Data
    public static class ModeLivraison {
        private CodeLibelle incoterme;
    }

    @Data
    public static class PaysCommerce {
        private CodeLibelle provenance;
        private CodeLibelle achat;
        private CodeLibelle premiereDestination;
        private CodeLibelle destinationDefinitive;
        private CodeLibelle origine;
    }

    @Data
    public static class Validite {
        private LocalDate dateDebut;
        private LocalDate dateFin;
    }

    @Data
    public static class Depot {
        private CodeLibelle organismeDepositaire;
        private String numero;
        private LocalDate date;
    }

    @Data
    public static class Domiciliation {
        private CodeLibelle organisme;
        private String numero;
        private String numeroCompte;
        private LocalDate date;
    }

    @Data
    public static class ReglementFinancier {
        private CodeLibelle mode;
        private JsonNode delai;
        private CodeLibelle devise;
    }

    // MONTANTS
    @Data
    public static class Montants {
        private MontantsDevise devise;
        private MontantsDinars dinars;
        private BigDecimal coursConversion;
    }

    @Data
    public static class MontantsDevise {
        private CodeLibelle devise;
        private BigDecimal ptfn;
        private BigDecimal fob;
    }

    @Data
    public static class MontantsDinars {
        private BigDecimal ptfn;
        private BigDecimal fob;
    }

    // OBLIGATION CAUTIONNEE
    @Data
    public static class ObligationCautionnee {
        private String numero;
        private LocalDateTime date;
    }

    // PAIEMENT
    @Data
    public static class Paiement {
        private BigDecimal montantPrincipal;
        private BigDecimal montantInteret;
        private BigDecimal montantTotal;
        private String montantTotalLettre;
        private BigDecimal montantRemise;
        private Integer delaiPaiement;
        private LocalDate dateEcheance;
    }

    // DECISION BANQUE
    @Data
    public static class DecisionBanque {
        private CodeLibelle decision;
        private String caution;
        private String motifRejet;
    }

    // QUITTANCE
    @Data
    public static class Quittance {
        private String numero;
        private LocalDate date;
    }

    // ANNULATION
    @Data
    public static class Annulation {
        private String motif;
    }

    // STATUTS
    @Data
    public static class Statuts {
        private CodeLibelle banque;
        private CodeLibelle ministereCommerce;
        private CodeLibelle organismeTechnique;
    }

    // OBSERVATIONS
    @Data
    public static class Observations {
        private List<JsonNode> generales;
        private List<JsonNode> banque;
        private List<JsonNode> ministereCommerce;
        private List<JsonNode> organismeTechnique;
    }

    // ARTICLES
    @Data
    public static class Article {
        private Integer numero;
        private String designation;
        private String nomenclature;
        private CodeLibelle uniteMesure;
        private BigDecimal quantite;
        private CodeLibelle paysOrigine;
        private CodeLibelle paysExportation;
        private BigDecimal prixFactureNet;
        private ValeurDinars valeurDinars;
        private CodeLibelle regimeStatistique;
        private ReglementFinancierArticle reglementFinancier;
        private ReferenceTce referenceTce;
        private StatutsArticle statuts;
        private Observations observations;
    }

    @Data
    public static class ValeurDinars {
        private BigDecimal fob;
        private BigDecimal douane;
    }

    @Data
    public static class ReglementFinancierArticle {
        private CodeLibelle mode;
        private JsonNode delai;
    }

    @Data
    public static class ReferenceTce {
        private String codeTce;
        private String numeroTce;
        private String codeOrganismeDomiciliataire;
        private String numeroGuichet;
        private String anneeDomiciliation;
        private String numeroDomiciliation;
    }

    @Data
    public static class StatutsArticle {
        private String banque;
        private String ministereCommerce;
        private String organismeTechnique;
    }

    // PIECES JOINTES
    @Data
    public static class PieceJointe {
        private Integer numero;
        private String typeDocument;
        private String numeroDocument;
        private LocalDate dateDocument;
        private String referenceFichierJoint;
        private String referenceBaseImage;
        private String nomFichier;
        private String path;
    }

    // ERREURS METIER
    @Data
    public static class Erreur {
        private String code;
        private String libelle;
        private String referenceDonnee;
        private Integer numeroOccurrence;
    }

    // TECHNIQUE
    @Data
    public static class Technique {
        private String idSeq;
        private String indicateurTransaction;
    }
}
