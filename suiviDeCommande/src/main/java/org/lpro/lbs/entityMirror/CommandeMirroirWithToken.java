package org.lpro.lbs.entityMirror;

import java.util.Date;


public class CommandeMirroirWithToken extends CommandeMirroir {

  
    private String token = "";

    
    public CommandeMirroirWithToken (String id, String nom, Date created, Date livraison, int status, String mail, String token) {
    	super(id, nom, created, livraison, status, mail);
    	this.token = token;
    }

    
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        System.out.println(token);
        this.token = token;
    }
    
}