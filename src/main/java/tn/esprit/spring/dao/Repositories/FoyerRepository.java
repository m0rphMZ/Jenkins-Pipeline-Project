package tn.esprit.spring.dao.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.TypeChambre;

import java.util.List;

public interface FoyerRepository extends JpaRepository<Foyer,Long> {
    Foyer findByNomFoyer(String nom);
    // select * from Foyer where capaciteFoyer > ....
    List<Foyer> findByCapaciteFoyerGreaterThan(int capacite);
    // select * from Foyer where capaciteFoyer < ....
    List<Foyer> findByCapaciteFoyerLessThan(int capacite);
    // select * from Foyer where capaciteFoyer < .... and capaciteFoyer> ....
    List<Foyer> findByCapaciteFoyerBetween(int min, int max);

    // Afficher le foyer de l'université dont son nom est passé en paramétre.
    // select f from Foyer f JOIN Universite u ON <Condition de jointure> where <condition>
    Foyer findByUniversiteNomUniversite(String nom);

    // Afficher la liste des foyers qui comportent des chambres de meme type
    // que le type passé en paramétre
    // Foyer -- Bloc -- Chambre
    List<Foyer> getByBlocsChambresTypeC(TypeChambre typeChambre);
}
