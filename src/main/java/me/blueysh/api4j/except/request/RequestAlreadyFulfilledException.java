package me.blueysh.api4j.except.request;

public class RequestAlreadyFulfilledException extends Exception {
    public RequestAlreadyFulfilledException(String m) {
        super(m);
    }
}
