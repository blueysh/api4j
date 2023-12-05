package me.blueysh.api4j.endpoint;

import com.sun.net.httpserver.HttpExchange;
import me.blueysh.api4j.endpoint.handler.GetHandler;
import me.blueysh.api4j.endpoint.handler.PostHandler;
import me.blueysh.api4j.except.handler.InvalidHandlerException;
import me.blueysh.api4j.request.RequestMethod;
import me.blueysh.api4j.except.endpoint.UnannotatedEndpointException;
import me.blueysh.api4j.request.Request;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("unused")
public abstract class Endpoint {

    public void receiveExchange(@NotNull HttpExchange exchange) {
        ensureValid();

        Request request = new Request(exchange);

        if (!Arrays.stream(getAllowedMethods()).toList().contains(request.getMethod())) handleInvalidMethod(request);
        else {
            try {
                switch (request.getMethod()) {
                    case GET -> get(request);
                    case POST -> post(request);
                }
            } catch (InvalidHandlerException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public abstract void handleInvalidMethod(@NotNull Request request);

    private void get(Request request) throws InvalidHandlerException {
        for (Method declaredMethod : getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(GetHandler.class)) {
                if (!Arrays.stream(declaredMethod.getParameterTypes()).toList().contains(Request.class) || declaredMethod.getParameterCount() != 1)
                    throw new InvalidHandlerException("Handlers must accept only one argument, of type Request.");

                declaredMethod.setAccessible(true);
                new Thread(() -> {
                    try {
                        declaredMethod.invoke(this, request);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }).start();


                return;
            }
        }

        throw new InvalidHandlerException("No GET handler is present in the Endpoint.");
    }

    private void post(Request request) throws InvalidHandlerException {
        for (Method declaredMethod : getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(PostHandler.class)) {
                if (!Arrays.stream(declaredMethod.getParameterTypes()).toList().contains(Request.class) || declaredMethod.getParameterCount() != 1)
                    throw new InvalidHandlerException("Handlers must accept only one argument, of type Request.");

                declaredMethod.setAccessible(true);
                new Thread(() -> {
                    try {
                        declaredMethod.invoke(this, request);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }).start();


                return;
            }
        }

        throw new InvalidHandlerException("No POST handler is present in the Endpoint.");
    }

    public String getPath() {
        ensureValid();
        return getClass().getAnnotation(EndpointData.class).path();
    }

    public String getDescription() {
        ensureValid();
        return getClass().getAnnotation(EndpointData.class).description();
    }

    public RequestMethod[] getAllowedMethods() {
        ensureValid();
        return getClass().getAnnotation(EndpointData.class).allowedMethods();
    }

    public void ensureValid() {
        if (!getClass().isAnnotationPresent(EndpointData.class)) {
            throw new UnannotatedEndpointException("Endpoints are required to be annotated with @EndpointData.");
        }
    }
}
