package com.GSTCP.ms_security.Models;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document
public class Session {
    @Id
    private String _id;
    private String token;
    private Date expiration;

    @DBRef //trabaja mongo como una base de datos relacional y no como una base de datos no relacional
    private User user;



    public Session(String token, Date expiration) {
        this.token = token;
        this.expiration = expiration;
    }


    public User getUser() {
        return user;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}