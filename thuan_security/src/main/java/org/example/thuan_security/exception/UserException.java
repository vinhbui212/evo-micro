package org.example.thuan_security.exception;

public class UserException extends Exception {
    String message;
    public UserException(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
