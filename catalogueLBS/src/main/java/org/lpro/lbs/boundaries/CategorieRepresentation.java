package org.lpro.lbs.boundaries;



import java.util.Optional;
import java.util.UUID;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.lpro.lbs.entity.Categorie;
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
import java.util.List;
import java.util.ArrayList;



@RestController
@RequestMapping(value="/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Categorie.class)
public class CategorieRepresentation {
    @Autowired
    private final CategorieRessource cr;
    @Autowired
    private final SandwichRessource sr;
    
    public CategorieRepresentation(CategorieRessource cr, SandwichRessource sr) {
        this.cr = cr;
        this.sr = sr;
    } 

    private Resources<Resource<Categorie>> categoriesToResource(Iterable<Categorie> categories) {
        Link selfLink = linkTo(CategorieRepresentation.class).withSelfRel();
        List<Resource<Categorie>> categorieResources = new ArrayList();
        categories.forEach(categorie
                -> categorieResources.add(categorieToResource(categorie, false)));
        return new Resources<>(categorieResources, selfLink);
    }
    
    private Resource<Categorie> categorieToResource(Categorie categorie, Boolean collection) {
        Link selfLink = linkTo(CategorieRepresentation.class)
                .slash(categorie.getId())
                .withSelfRel();
        if (collection) {
            Link collectionLink = linkTo(CategorieRepresentation.class).withRel("collection");
            Link sandwichsLink = linkTo(CategorieRepresentation.class).slash(categorie.getId()).slash("sandwichs").withRel("sandwichs");
            return new Resource<>(categorie, selfLink, collectionLink,sandwichsLink);
        } else {
            return new Resource<>(categorie, selfLink);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        Iterable<Categorie> allCategories = cr.findAll();
        return new ResponseEntity<>(categoriesToResource(allCategories),HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getCategorieWithId (@PathVariable("id") String id) throws NotFound{
        return Optional.ofNullable(cr.findById(id))
                .filter(Optional::isPresent)
                .map(categorie -> new ResponseEntity<>(categorieToResource(categorie.get(),true),HttpStatus.OK))
                .orElseThrow(() -> new NotFound("Categorie not found"));
    }
    
    @GetMapping(value="/{id}/sandwichs")
    public ResponseEntity<?> getSandwichsCategorieWithId (@PathVariable("id") String id) throws NotFound{
        return Optional.ofNullable(cr.findById(id))
                .filter(Optional::isPresent)
                .map(categorie -> new ResponseEntity<>(sr.findByCategorieId(id), HttpStatus.OK))
                .orElseThrow(() -> new NotFound("Categorie not found"));
    }


    @PostMapping
    public ResponseEntity<?> postCategorie(@RequestBody Categorie ctg) throws BadRequest{
        ctg.setId(UUID.randomUUID().toString());
        String errors = ctg.isValid();
        if(errors.isEmpty()){
            Categorie newCtg = cr.save(ctg);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setLocation(linkTo(CategorieRepresentation.class).slash(newCtg.getId()).toUri());
            return new ResponseEntity<>(categorieToResource(newCtg, true),responseHeaders,HttpStatus.CREATED);
        } else {
            throw new BadRequest(errors);
        }
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<?> putCategorie(@RequestBody Categorie categorieUpdated, @PathVariable("id") String id) throws BadRequest,NotFound{
        return cr.findById(id)
                .map(categorie -> {
                    categorie.setId(categorie.getId());
                    categorie.setDesc(categorieUpdated.getDesc());
                    categorie.setNom(categorieUpdated.getNom());
                    String errors = categorie.isValid();
                    if(errors.isEmpty()){
                        cr.save(categorie);
                        HttpHeaders responseHeaders = new HttpHeaders();
                        responseHeaders.setLocation(linkTo(CategorieRepresentation.class).slash(categorie.getId()).toUri());
                        return new ResponseEntity<>(categorie, responseHeaders, HttpStatus.CREATED);
                    } else throw new BadRequest(errors);
                }).orElseThrow(() -> new NotFound("Intervenant inexistant"));
    }

    @DeleteMapping(value="/id")
    public ResponseEntity<?> deleteCategorie(@PathVariable("id") String id) throws NotFound,MethodNotAllowed{
        return cr.findById(id).map( categorie -> {
            cr.delete(categorie);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }).orElseThrow( () -> new NotFound("Categorie not found"));
    }
}



