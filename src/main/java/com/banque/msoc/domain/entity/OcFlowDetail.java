package com.banque.msoc.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OC_FLOW_DETAIL")
public class OcFlowDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_flow_detail_seq")
    @SequenceGenerator(name = "oc_flow_detail_seq", sequenceName = "SEQ_OC_FLOW_DETAIL", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FLOW_ID", nullable = false, unique = true)
    private OcFlow flow;

    @Column(name = "COD_TYP_MES", length = 50) private String codTypMes;
    @Column(name = "COD_TYP_DOC", length = 50) private String codTypDoc;
    @Column(name = "ETAT", length = 20) private String etat;

    @Column(name = "NUM_MESS_TTN", length = 50) private String numMessTtn;
    @Column(name = "NUM_DOSS_TTN", length = 50) private String numDossTtn;
    @Column(name = "NUM_DEM_TTN", length = 50) private String numDemTtn;

    @Column(name = "EMETTEUR", length = 50) private String emetteur;
    @Column(name = "DESTINATAIRE", length = 50) private String destinataire;

    @Column(name = "COD_DOU_IMP", length = 50) private String codDouImp;
    @Column(name = "RAI_SOC_IMP", length = 200) private String raiSocImp;
    @Column(name = "ADRESSE_IMP", length = 500) private String adresseImp;

    @Column(name = "COD_TTN_DEC", length = 50) private String codTtnDec;
    @Column(name = "NOM_SIG_DEC", length = 100) private String nomSigDec;
    @Column(name = "DAT_DEC") private LocalDate datDec;

    @Column(name = "COD_BUR_DOU", length = 50) private String codBurDou;
    @Column(name = "LIB_BUR_DOU", length = 200) private String libBurDou;
    @Column(name = "COD_CPT", length = 50) private String codCpt;

    @Column(name = "NUM_REP_DDM", length = 50) private String numRepDdm;

    @Column(name = "NUM_DEC_DDM", length = 50) private String numDecDdm;
    @Column(name = "DAT_DEC_DDM") private LocalDate datDecDdm;

    @Column(name = "COD_BQ_IMP", length = 50) private String codBqImp;
    @Column(name = "LIB_BQ_IMP", length = 50) private String libBqImp;
    @Column(name = "COD_ORG_IMP", length = 50) private String codOrgImp;
    @Column(name = "LIB_ORG_IMP", length = 50) private String libOrgImp;
    @Column(name = "NUM_RIB", length = 150) private String numRib;

    @Column(name = "NUM_ENR_OC", length = 50) private String numEnrOc;
    @Column(name = "DAT_ENR_OC") private LocalDate datEnrOc;

    @Column(name = "MONT_PRINCIPAL", precision = 18, scale = 3) private BigDecimal montPrincipal;
    @Column(name = "MONT_INTERET", precision = 18, scale = 3) private BigDecimal montInteret;
    @Column(name = "MONT_TOT", precision = 18, scale = 3) private BigDecimal montTot;
    @Column(name = "MONT_LETTRE", length = 500) private String montLettre;
    @Column(name = "MONT_REMISE", precision = 18, scale = 3) private BigDecimal montRemise;
    @Column(name = "DELAI_PAIE", length = 20) private String delaiPaie;
    @Column(name = "DAT_ECH") private LocalDate datEch;

    @Column(name = "COD_DEC_BQ", length = 50) private String codDecBq;
    @Column(name = "LIB_DEC", length = 200) private String libDec;
    @Column(name = "LIB_CAUTION", length = 500) private String libCaution;
    @Column(name = "NOM_ORG_BQ", length = 200) private String nomOrgBq;
    @Column(name = "NOM_SIG_BQ", length = 100) private String nomSigBq;
    @Column(name = "DAT_SIG_BQ") private LocalDate datSigBq;
    @Column(name = "MOTIF_REJET", length = 500) private String motifRejet;

    @Column(name = "NUM_QUITTANCE", length = 50) private String numQuittance;
    @Column(name = "DAT_QUITTANCE") private LocalDate datQuittance;

    @Column(name = "NOM_SIG_REC", length = 100) private String nomSigRec;
    @Column(name = "DAT_SIG_REC") private LocalDate datSigRec;

    @Column(name = "ID_SEQ", length = 50) private String idSeq;
    @Column(name = "IND_TRANSACT", length = 10) private String indTransact;
    @Column(name = "MOTIF_ANNUL", length = 500) private String motifAnnul;
}
