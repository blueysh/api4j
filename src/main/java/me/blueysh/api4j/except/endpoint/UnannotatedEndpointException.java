package me.blueysh.api4j.except.endpoint;

public class UnannotatedEndpointException extends RuntimeException {
    public UnannotatedEndpointException(String m) {
        super(m);
    }
}
