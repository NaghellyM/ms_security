package com.GSTCP.ms_security.Models;

public class NotificationRequest {

    private String subject;

    private String recipient;

    private String body_html;

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
    }

    public String getSubject() {
        return subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getBody_html() {
        return body_html;
    }
}
