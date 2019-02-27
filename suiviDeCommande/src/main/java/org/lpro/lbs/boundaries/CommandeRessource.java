package org.lpro.lbs.boundaries;

import java.util.List;
import java.util.Optional;

import org.lpro.lbs.entity.Commande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRessource extends JpaRepository<Commande, String> {

    List<Commande> findAllByOrderByCreatedAtAscLivraisonAsc(Pageable page);

    List<Commande> findByStatusEqualsOrderByCreatedAtAscLivraisonAsc(int status , Pageable page);
    
    Optional<Commande> findByIdAndToken(String id, String token);
}