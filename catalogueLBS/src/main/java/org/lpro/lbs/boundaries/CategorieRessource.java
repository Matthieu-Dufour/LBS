package org.lpro.lbs.boundaries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

import org.lpro.lbs.entity.Categorie;

public interface CategorieRessource extends JpaRepository<Categorie, String> {
	Set<Categorie> findBySandwichId(String sandwichId);	
}