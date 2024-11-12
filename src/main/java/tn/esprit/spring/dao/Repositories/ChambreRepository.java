package tn.esprit.spring.dao.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;

import java.time.LocalDate;
import java.util.List;

public interface ChambreRepository extends JpaRepository<Chambre, Long> {
    Chambre findByNumeroChambre(long num);

    List<Chambre> findByBlocNomBloc(String nom);

    int countByTypeCAndBlocIdBloc(TypeChambre typeChambre, long idBloc);

    //********************* Ajouter Reservation *********************
    //SQL
    @Query(value = "select count(*) from t_chambre c " +
            "join t_chambre_reservations cr on c.id_chambre=cr.chambre_id_chambre " +
            "join t_reservation r on cr.reservations_id_reservation=r.id_reservation " +
            "where c.id_chambre=?1 and r.annee_universitaire between ?2 and ?3", nativeQuery = true)
    int listerReservationPourUneChambre(long idChambre, LocalDate dateDebutAU, LocalDate dateFinAU);

    //Keyword
    int countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(long chambreId, LocalDate dateDebutAU, LocalDate dateFinAU);

    //*****************************************************************
    Chambre findByReservationsIdReservation(String idReservation);

    long count();

    long countChambreByTypeC(TypeChambre typeChambre);

    long countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(long idChambre, boolean estValide, LocalDate dateDebut, LocalDate dateFin);
//    List<Chambre> findAllByNumeroChambre(List<Long> num);
}
