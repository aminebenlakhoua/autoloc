package tn.esprit.autoloc.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AssociationsTest {

    @Autowired
    private EntityManager em;

    private Contrat nouveauContrat() {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(new BigDecimal("300.00"));
        contrat.setValide(true);
        return contrat;
    }

    private Paiement nouveauPaiement(Contrat contrat) {
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal("100.00"));
        paiement.setDatePaiement(LocalDate.now());
        paiement.setModePaiement(ModePaiement.CARTE);
        paiement.setContrat(contrat);
        return paiement;
    }

    @Test
    void cascadeAll_enregistre_les_paiements_avec_le_contrat() {
        Contrat contrat = nouveauContrat();
        Paiement paiement = nouveauPaiement(contrat);
        contrat.getPaiements().add(paiement);

        em.persist(contrat);
        em.flush();

        assertNotNull(paiement.getIdPaiement());
    }

    @Test
    void supprimer_un_contrat_supprime_ses_paiements() {
        Contrat contrat = nouveauContrat();
        Paiement paiement = nouveauPaiement(contrat);
        contrat.getPaiements().add(paiement);
        em.persist(contrat);
        em.flush();
        Long idPaiement = paiement.getIdPaiement();

        em.remove(contrat);
        em.flush();
        em.clear();

        assertNull(em.find(Paiement.class, idPaiement));
    }

    @Test
    void orphanRemoval_supprime_le_paiement_retire_de_la_liste() {
        Contrat contrat = nouveauContrat();
        Paiement paiement = nouveauPaiement(contrat);
        contrat.getPaiements().add(paiement);
        em.persist(contrat);
        em.flush();
        Long idPaiement = paiement.getIdPaiement();
        Long idContrat = contrat.getIdContrat();

        contrat.getPaiements().remove(paiement);
        em.flush();
        em.clear();

        assertNull(em.find(Paiement.class, idPaiement));
        assertNotNull(em.find(Contrat.class, idContrat));
    }

    @Test
    void manyToMany_remplit_la_table_de_jointure() {
        Equipement gps = new Equipement();
        gps.setLibelle("GPS");
        em.persist(gps);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("TEST-123");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("50.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.getEquipements().add(gps);
        em.persist(vehicule);
        em.flush();

        Number lignes = (Number) em
                .createNativeQuery("select count(*) from vehicule_equipement where id_vehicule = ?1")
                .setParameter(1, vehicule.getIdVehicule())
                .getSingleResult();

        assertEquals(1L, lignes.longValue());
    }
}