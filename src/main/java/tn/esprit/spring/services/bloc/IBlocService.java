package tn.esprit.spring.services.bloc;

import tn.esprit.spring.dao.entities.Bloc;

import java.util.List;

public interface IBlocService {
    Bloc addOrUpdate(Bloc b);

    List<Bloc> findAll();

    Bloc findById(long id);

    void deleteById(long id);

    void delete(Bloc b);

    Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc);
    Bloc affecterBlocAFoyer( String nomBloc,  String nomFoyer) ;


}
