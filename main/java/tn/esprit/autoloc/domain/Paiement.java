package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonBackReference; // ← AJOUT IMPORTANT
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paiement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaiement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModePaiement modePaiement;

    // Annotation ajoutée pour gérer la sérialisation JSON bidirectionnelle
    // Le nom "contrat-paiements" doit correspondre EXACTEMENT à celui dans Contrat.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id") // Assure-toi que ce nom correspond à ta colonne en base
    @JsonBackReference("contrat-paiements")
    private Contrat contrat;
}