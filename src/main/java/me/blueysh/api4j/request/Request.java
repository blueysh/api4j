package me.blueysh.api4j.request;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import me.blueysh.api4j.except.request.RequestAlreadyFulfilledException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;

@SuppressWarnings("unused")
public class Request {
    private final RequestMethod method;
    private boolean isFulfilled;
    private final Headers headers;
    private final HttpExchange exchange;
    private final String content;
    private final URI uri;

    public Request(@NotNull HttpExchange exchange) {
        this.exchange = exchange;
        this.method = RequestMethod.of(exchange.getRequestMethod());
        this.isFulfilled = false;
        this.headers = exchange.getRequestHeaders();
        this.uri = exchange.getRequestURI();
        try {
            this.content = new String(exchange.getRequestBody().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestMethod getMethod() {
        return method;
    }

    public boolean isFulfilled() {
        return isFulfilled;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getContent() {
        return content;
    }

    public URI getUri() {
        return uri;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public void fulfill(@NotNull Response response) throws RequestAlreadyFulfilledException, IOException {
        if (isFulfilled) throw new RequestAlreadyFulfilledException("This request has already been fulfilled.");

        response.getHeaders().forEach((h, v) -> exchange.getResponseHeaders().set(h, v.get(0)));

        exchange.sendResponseHeaders(response.getCode().code(), response.getContent().length());

        exchange.getResponseBody().write(response.getContent().getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();

        isFulfilled = true;
    }
}
