package org.lpro.lbs.boundaries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

import org.lpro.lbs.entity.Sandwich;

public interface SandwichRessource extends JpaRepository<Sandwich, String> {
	Set<Sandwich> findByCategorieId(String categorieId);	
}