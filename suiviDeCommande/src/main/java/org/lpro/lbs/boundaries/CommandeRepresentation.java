package org.lpro.lbs.boundaries;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.lpro.lbs.entity.Commande;
import org.lpro.lbs.entityMirror.CommandeMirroir;
import org.lpro.lbs.entityMirror.CommandeMirroirWithToken;
import org.lpro.lbs.exception.BadRequest;
import org.lpro.lbs.exception.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@RestController
@RequestMapping(value="/commandes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Commande.class)
public class CommandeRepresentation {
    @Autowired
    private final CommandeRessource cr;

    public CommandeRepresentation(CommandeRessource cr) {
        this.cr = cr;
    } 

     private Resources<Resource<CommandeMirroir>> commandesToResource(Iterable<CommandeMirroir> commandes) {
        Link selfLink = linkTo(CommandeRepresentation.class).withSelfRel();
        List<Resource<CommandeMirroir>> commandeResources = new ArrayList();
        commandes.forEach(commande
                -> commandeResources.add(commandeToResource(commande, false)));
        return new Resources<>(commandeResources, selfLink);
    }
    
    private Resource<CommandeMirroir> commandeToResource(CommandeMirroir commande, Boolean collection) {
        Link selfLink = linkTo(CommandeRepresentation.class)
                .slash(commande.getId())
                .withSelfRel();
        if (collection) {
            Link collectionLink = linkTo(CommandeRepresentation.class).withRel("collection");
            return new Resource<>(commande, selfLink, collectionLink);
        } else {
            return new Resource<>(commande, selfLink);
        }
    }
    
    private CommandeMirroir commandeToMirror(Commande c, Boolean showToken) {
    	CommandeMirroir cm = null;
    	if(showToken) {
    		cm = new CommandeMirroirWithToken(c.getId(), c.getNom(), c.getcreatedAt(), c.getLivraison(), c.getStatus(), c.getMail(), c.getToken());
    	}else {
    		 cm = new CommandeMirroir(c.getId(), c.getNom(), c.getcreatedAt(), c.getLivraison(), c.getStatus(), c.getMail());
    	}
    	return cm;
    }

//@RequestParam(value="page", required=false)Optional<Integer> page,
          //  @RequestParam(value="limit", required=false)Optional<Integer> limit
    @GetMapping
    public ResponseEntity<?> getAllCommandes(
        @RequestParam(value="status",required=false)Optional<Integer> status,
        @RequestParam(value="page", required=false)Optional<Integer> page,
        @RequestParam(value="limit", required=false,defaultValue="10")int limit) throws BadRequest {
            PageRequest pageable = null;
            if(page.isPresent() && page.get() > 0){
                pageable = PageRequest.of(page.get(), limit);
            } else {
                pageable = PageRequest.of(1, limit);
            }
            List<Commande> allCommandes = null;
            if(status.isPresent()){
                if(status.get() > 0 && status.get() <= 4){
                    allCommandes = cr.findByStatusEqualsOrderByCreatedAtAscLivraisonAsc(status.get(),pageable);
                } else throw new BadRequest("Veuillez choisir un statut de commande valide");
            } else {
                allCommandes = cr.findAllByOrderByCreatedAtAscLivraisonAsc(pageable);
            }
            
            Iterable<CommandeMirroir> cm = allCommandes.stream()
    										.map(commande -> commandeToMirror(commande, false))
    										.collect(Collectors.toList());
    
            return new ResponseEntity<>(commandesToResource(cm),HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getCommandeWithId (@PathVariable("id") String id, @RequestHeader(value = "x-lbs-token") String headerToken) throws NotFound{
    	Optional<Commande> commande = cr.findByIdAndToken(id, headerToken);
    	
    	if(commande.isPresent()) {
    		CommandeMirroir cm = commandeToMirror(commande.get(), false);
    		return new ResponseEntity<>(cm ,HttpStatus.OK);
    	}
    	
        throw new NotFound("Commande not found");
    }

    @PostMapping
    public ResponseEntity<?> postCommande(@RequestBody Commande ctg) throws BadRequest{
        ctg.setId(UUID.randomUUID().toString());
        String errors = ctg.isValid();

        String jwtToken;

        if(errors.isEmpty()){
            Commande newCtg = cr.save(ctg);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setLocation(linkTo(CommandeRepresentation.class).slash(newCtg.getId()).toUri());

            jwtToken = Jwts.builder().setSubject(newCtg.getId()).claim("roles", "commande").setIssuedAt(new Date()) //rajouter un param dans setIssuedAt pour l'expiration
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact();

            newCtg.setToken(jwtToken);
            
            CommandeMirroir cm = commandeToMirror(newCtg, true); 
            return new ResponseEntity<>(commandeToResource(cm, true),responseHeaders,HttpStatus.CREATED);
        } else {
            throw new BadRequest(errors);
        }
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<?> putCommande(@RequestBody Commande commandeUpdated, @PathVariable("id") String id, @RequestHeader(value = "x-lbs-token") String headerToken) throws BadRequest,NotFound{
        
    	Optional<Commande> commande = cr.findByIdAndToken(id, headerToken);
    	
    	if(commande.isPresent()) {
    		Commande c = commande.get();
    		String errors = c.isValid();
    		
    		if(errors.isEmpty()){
                cr.save(c);
                CommandeMirroir cm = commandeToMirror(c, false);
                
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setLocation(linkTo(CommandeRepresentation.class).slash(c.getId()).toUri());
                return new ResponseEntity<>(commandeToResource(cm, true), responseHeaders, HttpStatus.CREATED);
            } else throw new BadRequest(errors);
    		
    	}
    	
    	throw new NotFound("Intervenant inexistant");
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<?> deleteCommande(@PathVariable("id") String id) {
        Commande commande = cr.findById(id).orElseThrow(() -> new NotFound("Commande inexistante"));
        cr.delete(commande);
        return new ResponseEntity<>(commande, HttpStatus.OK);
    }

}



