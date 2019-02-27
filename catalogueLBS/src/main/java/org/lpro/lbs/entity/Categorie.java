package org.lpro.lbs.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Set;
import java.util.HashSet;

import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categorie")
public class Categorie {

    @Id
    private String id;
    private String nom;
    private String desc;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "categorie_sandwich", 
      joinColumns = @JoinColumn(name = "categorie_id", referencedColumnName = "id"), 
      inverseJoinColumns = @JoinColumn(name = "sandwich_id", referencedColumnName = "id"))
    @JsonIgnore
    private Set<Sandwich> sandwichs = new HashSet<>();

    public Categorie() {}

    public Categorie( String nom, String desc) {
        this.nom = nom;
        this.desc = desc;
    }

    public Set<Sandwich> getSandwichs() {
        return this.sandwichs;
    }

    public void setSandwichs(Set<Sandwich> sandwichs) {
        this.sandwichs = sandwichs;
    }


    public String getId(){
        return this.id;
    }

    public void setId(String id){
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

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", nom='" + getNom() + "'" +
            ", desc='" + getDesc() + "'" +
            "}";
    }

    public String isValid(){
        String valid = "";
        if(this.nom == null || this.nom.isEmpty()){
            valid += "le nom de la catégorie doit être défini"+System.getProperty("line.separator");
        }
        if(this.desc == null || this.desc.isEmpty()){
            valid += "Veuillez mettre une description pour cette catégorie"+System.getProperty("line.separator");
        }
        return valid;
    }
    

}