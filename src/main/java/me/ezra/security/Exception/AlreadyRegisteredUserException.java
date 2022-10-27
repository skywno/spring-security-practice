package me.ezra.security.Exception;

public class AlreadyRegisteredUserException extends RuntimeException{

    public AlreadyRegisteredUserException() {
        super("이미 등록된 유저입니다.");
    }

    public AlreadyRegisteredUserException(String message) {
        super(message);
    }
}
