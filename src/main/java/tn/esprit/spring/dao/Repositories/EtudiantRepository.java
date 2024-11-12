package tn.esprit.spring.dao.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.dao.entities.Etudiant;

import java.util.List;

public interface EtudiantRepository extends JpaRepository<Etudiant,Long> {
    //select * from Etudiant where cin=... 
    Etudiant findByCin(long cin);

    // select * from Etudiant where nomEt like ...
    List<Etudiant> findByNomEtLike(String nom);
    List<Etudiant> findByNomEtContains(String nom);
    List<Etudiant> findByNomEtContaining(String nom);

    @Query(value = "select e from t_etudiant e " +
            "           join t_reservation_etudiants re on e.id_etudiant=re.etudiants_id_etudiant" +
            "           join t_reservation r on r.id_reservation= re.reservations_id_reservation" +
            "           where r.est_valide=?1 ", nativeQuery = true)
    List<Etudiant> e5erMethodeSQL(boolean estValide);
}
