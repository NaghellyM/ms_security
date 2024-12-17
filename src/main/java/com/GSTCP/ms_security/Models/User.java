package com.GSTCP.ms_security.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class User {
    @Id
    private String _id;
    private String name;
    private String email;
    private String password;
    private String verificationCode;
        private String CaptchaToken;


    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setCaptchaToken(String CaptchaToken) {
        this.CaptchaToken = CaptchaToken;
    }

    public String getCaptchaToken() {
        return CaptchaToken;
    }

    // private List<Role> role;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // constructor por defecto
    public User() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
