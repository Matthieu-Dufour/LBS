package org.lpro.lbs.entityMirror;

import java.util.Date;


public class CommandeMirroir {

    private String id;
    private String nom;
    private Date createdAt;
    private Date livraison;
    private int status;
    private String mail;


    public CommandeMirroir (String id, String nom, Date created, Date livraison, int status, String mail) {
    	this.id = id;
        this.nom  = nom;
        this.createdAt = created;
        this.livraison = livraison;
        this.status = status;
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
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
}