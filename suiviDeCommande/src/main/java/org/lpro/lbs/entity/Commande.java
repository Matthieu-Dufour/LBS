package org.lpro.lbs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "commande")
public class Commande {

    @Id
    private String id;
    private String nom;
    @Column(name="created_at", nullable=false)
    private Date createdAt;
    private Date livraison;
    private int status;
    private String token;
    private String mail;

    public Commande () {}

    public Commande (String nom, Date livraison, String mail) {
        //SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        this.nom  = nom;
        this.createdAt = new Date();
        this.livraison = livraison;
        this.status = 1;
        this.mail = mail;
    }

    public Date getcreatedAt() {
		return createdAt;
	}

	public void setcreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getLivraison() {
		return livraison;
	}

	public void setLivraison(Date livraison) {
		this.livraison = livraison;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

    
    public String isValid(){
        String valid = "";
        if(this.nom == null || this.nom.isEmpty()){
            valid += "le nom de la commande doit être défini"+System.getProperty("line.separator");
        }
        return valid;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        System.out.println(token);
        this.token = token;
    }

    /**
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
}