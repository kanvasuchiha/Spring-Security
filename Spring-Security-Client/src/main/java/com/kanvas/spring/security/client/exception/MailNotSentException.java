package com.kanvas.spring.security.client.exception;

public class MailNotSentException extends RuntimeException{

    public MailNotSentException(String message, Exception exception) {
        super(message, exception);
    }

    public MailNotSentException(String message) {
        super(message);
    }
}
