package org.lpro.lbs.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sandwich")
public class Sandwich {

    @Id
    private String id;
    private String nom;
    private String desc;
    private Double prix;
    
    @ManyToMany(mappedBy = "sandwichs", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Categorie> categories;

    public Sandwich () {}

    public Sandwich (String nom, String desc, Double prix) {
        this.nom  = nom;
        this.desc = desc;
        this.prix = prix;
    }

    public Set<Categorie> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<Categorie> categories) {
        this.categories = categories;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getPrix() {
        return this.prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }
    
    public String isValid(){
        String valid = "";
        if(this.nom == null || this.nom.isEmpty()){
            valid += "le nom du sandwich doit être défini"+System.getProperty("line.separator");
        }
        if(this.desc == null || this.desc.isEmpty()){
            valid += "Veuillez mettre une description pour ce sandwich"+System.getProperty("line.separator");
        }
        if(this.prix == null || this.prix <= 0.00){
            valid += "Veuillez mettre un prix pour ce sandwich"+System.getProperty("line.separator");
        }
        return valid;
    }
}