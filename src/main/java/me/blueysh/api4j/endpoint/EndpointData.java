package me.blueysh.api4j.endpoint;

import me.blueysh.api4j.request.RequestMethod;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EndpointData {
    /**
     * @return The path of the endpoint.
     */
    @NotNull String path();

    /**
     * @return The description for the endpoint. This can be empty.
     */
    @NotNull String description();

    /**
     * @return A list of allowed {@link RequestMethod}s the endpoint can be used with.
     */
    @NotNull RequestMethod[] allowedMethods();
}
