package me.blueysh.api4j.request;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Response {
    private ResponseCode code;
    private final Headers headers;
    private String content;

    public Response(@NotNull ResponseCode code, @NotNull String content) {
        this.code = code;
        this.headers = new Headers();
        this.content = content;
    }

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(@NotNull ResponseCode code) {
        this.code = code;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
