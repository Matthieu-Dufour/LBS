package org.lpro.lbs.boundaries;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Optional;
import java.util.UUID;

import org.lpro.lbs.entity.Sandwich;
import org.lpro.lbs.exception.BadRequest;
import org.lpro.lbs.exception.MethodNotAllowed;
import org.lpro.lbs.exception.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import java.util.ArrayList;
import java.util.List;

//Permet de définir un controller REST
@RestController
// Définition d'une route par défaut
@RequestMapping(value="/sandwichs", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Sandwich.class)
public class SandwichRepresentation {
    @Autowired
    private final SandwichRessource sr;
    @Autowired
    private final CategorieRessource cr;

    public SandwichRepresentation(SandwichRessource sr, CategorieRessource cr){
        this.sr = sr;
        this.cr = cr;
    }

    private Resources<Resource<Sandwich>> sandwichsToResource(Iterable<Sandwich> sandwichs) {
        Link selfLink = linkTo(SandwichRepresentation.class).withSelfRel();
        List<Resource<Sandwich>> sandwichsResources = new ArrayList();
        sandwichs.forEach(sandwich
                -> sandwichsResources.add(sandwichToResource(sandwich, false)));
        return new Resources<>(sandwichsResources, selfLink);
    }
    
    private Resource<Sandwich> sandwichToResource(Sandwich sandwich, Boolean collection) {
        Link selfLink = linkTo(SandwichRepresentation.class)
                .slash(sandwich.getId())
                .withSelfRel();
        if (collection) {
            Link collectionLink = linkTo(SandwichRepresentation.class).withRel("collection");
            Link categoriesLink = linkTo(SandwichRepresentation.class).slash(sandwich.getId()).slash("categories").withRel("categories");
            return new Resource<>(sandwich, selfLink, collectionLink,categoriesLink);
        } else {
            return new Resource<>(sandwich, selfLink);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSandwichs(){
        Iterable<Sandwich> allSandwich = sr.findAll();
        return new ResponseEntity<>(sandwichsToResource(allSandwich),HttpStatus.OK);
    }

    @GetMapping(value ="/{id}")
    public ResponseEntity<?> getSandwich (@PathVariable("id") String id) throws NotFound{
        return Optional.ofNullable(sr.findById(id))
                .filter(Optional::isPresent)
                .map(sandwich -> new ResponseEntity<>(sandwichToResource(sandwich.get(),true),HttpStatus.OK))
                .orElseThrow(() -> new NotFound("Sandwich not found"));
    }
    
    @GetMapping(value="/{id}/categories")
    public ResponseEntity<?> getCategoriesSandwichWithId (@PathVariable("id") String id) throws NotFound{
        return Optional.ofNullable(sr.findById(id))
                .filter(Optional::isPresent)
                .map(categorie -> new ResponseEntity<>(cr.findBySandwichId(id), HttpStatus.OK))
                .orElseThrow(() -> new NotFound("Categorie not found"));
    }

    @PostMapping
    public ResponseEntity<?> postSandwich(@RequestBody Sandwich sandwich) throws BadRequest{
        sandwich.setId(UUID.randomUUID().toString());
        String errors = sandwich.isValid();
        if(errors.isEmpty()){
            Sandwich newSw = sr.save(sandwich);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setLocation(linkTo(SandwichRepresentation.class).slash(newSw.getId()).toUri());
            return new ResponseEntity<>(newSw,responseHeaders,HttpStatus.CREATED);
        } else {
            throw new BadRequest(errors);
        }
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<?> putSandwich(@RequestBody Sandwich sandwichUpdated, @PathVariable("id") String id) throws BadRequest,NotFound{
        return sr.findById(id)
                .map(sandwich -> {
                    sandwich.setId(sandwich.getId());
                    sandwich.setDesc(sandwichUpdated.getDesc());
                    sandwich.setNom(sandwichUpdated.getNom());
                    sandwich.setPrix(sandwichUpdated.getPrix());
                    String errors = sandwich.isValid();
                    if(errors.isEmpty()){
                        sr.save(sandwich);
                        HttpHeaders responseHeaders = new HttpHeaders();
                        responseHeaders.setLocation(linkTo(SandwichRepresentation.class).slash(sandwich.getId()).toUri());
                        return new ResponseEntity<>(sandwich, responseHeaders, HttpStatus.CREATED);
                    } else throw new BadRequest(errors);
                }).orElseThrow(() -> new NotFound("Intervenant inexistant"));
    }

    @DeleteMapping(value="/id")
    public ResponseEntity<?> deleteSandwich(@PathVariable("id") String id) throws NotFound,MethodNotAllowed{
        return sr.findById(id).map( sandwich -> {
            sr.delete(sandwich);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }).orElseThrow( () -> new NotFound("Sandwich not found"));
    }

}