package me.blueysh.api4j;

import com.sun.net.httpserver.HttpServer;
import me.blueysh.api4j.endpoint.Endpoint;
import me.blueysh.api4j.except.endpoint.EndpointAlreadyExistsException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class APIServer {
    private final HttpServer _server;
    private boolean isRunning;
    private final HashMap<String, Endpoint> endpoints;

    private APIServer(InetSocketAddress addr, int backlog, int nThreads) throws IOException {
        this._server = HttpServer.create(addr, backlog);
        this.endpoints = new HashMap<>();

        _server.setExecutor(Executors.newFixedThreadPool(nThreads));
    }

    /**
     * Creates a new {@link APIServer}. Uses default socket backlog value of {@code 0}. Uses default thread number of {@code 1}.
     *
     * @param addr The address the server will attempt to bind to. If {@code null}, {@link #bind(InetSocketAddress, int)} must be called to set it.
     * @return An {@link APIServer} instance.
     * @throws IOException If an I/O error occurs.
     */
    public static APIServer create(InetSocketAddress addr) throws IOException {
        return new APIServer(addr, 0, 1);
    }

    /**
     * Creates a new {@link APIServer}.
     *
     * @param addr    The address the server will attempt to bind to. If {@code null}, {@link #bind(InetSocketAddress, int)} must be called to set it.
     * @param backlog The socket backlog value.
     * @param nThreads The number of threads the server will use.
     * @return An {@link APIServer} instance.
     * @throws IOException If an I/O error occurs.
     */
    public static APIServer create(InetSocketAddress addr, int backlog, int nThreads) throws IOException {
        return new APIServer(addr, backlog, nThreads);
    }

    /**
     * @return The address of the server.
     */
    public InetSocketAddress getAddr() {
        return _server.getAddress();
    }

    /**
     * Binds the server to an address.
     *
     * @param addr    The address to attempt to bind to.
     * @param backlog The socket backlog value.
     * @throws IOException If an I/O error occurs.
     */
    public void bind(@NotNull InetSocketAddress addr, int backlog) throws IOException {
        _server.bind(addr, backlog);
    }

    /**
     * Starts the server in a new background thread.
     */
    public void start() {
        _server.start();
        isRunning = true;
    }

    /**
     * Stops the server immediately.
     */
    public void stop() {
        _server.stop(0);
        isRunning = false;
    }

    /**
     * Stops the server.
     *
     * @param delay Time in seconds to wait until exchanges have completed.
     */
    public void stop(int delay) {
        _server.stop(delay);
        isRunning = false;
    }

    /**
     * @return Self-explanatory.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Registers an {@link Endpoint} to the server.
     *
     * @param endpoint The endpoint to register.
     */
    public void registerEndpoint(@NotNull Endpoint endpoint) throws EndpointAlreadyExistsException {
        if (this.endpoints.containsKey(endpoint.getPath()))
            throw new EndpointAlreadyExistsException("An Endpoint with " + endpoint.getPath() + " already exists.");

        endpoint.ensureValid();
        endpoints.put(endpoint.getPath(), endpoint);
        _server.createContext(endpoint.getPath(), endpoint::receiveExchange);
    }

    /**
     * Registers {@link Endpoint}s to the server.
     *
     * @param endpoints The endpoints to register.
     */
    public void registerEndpoints(@NotNull Endpoint... endpoints) throws EndpointAlreadyExistsException {
        for (Endpoint endpoint : endpoints) {
            if (this.endpoints.containsKey(endpoint.getPath()))
                throw new EndpointAlreadyExistsException("An Endpoint with " + endpoint.getPath() + " already exists.");

            registerEndpoint(endpoint);
        }
    }

    /**
     * Fetches an {@link Endpoint} by the path it is registered to.
     *
     * @param path The path of the endpoint.
     * @return The endpoint registered with the provided path. {@code null} if none exists.
     */
    public Endpoint getEndpointByPath(@NotNull String path) {
        return endpoints.get(path);
    }

    /**
     * Fetches all {@link Endpoint}s with paths containing the search string.
     *
     * @param searchString The search string to use.
     * @return A list of qualifying endpoints.
     */
    public List<Endpoint> getEndpointsByPath(@NotNull String searchString) {
        List<Endpoint> qualifyingEndpoints = new ArrayList<>();

        endpoints.forEach((path, endpoint) -> {
            if (path.contains(searchString)) qualifyingEndpoints.add(endpoint);
        });

        return qualifyingEndpoints;
    }

    /**
     * Unregisters an {@link Endpoint} from the server.
     *
     * @param path The path of the endpoint.
     */
    public void unregisterEndpointByPath(@NotNull String path) {
        endpoints.remove(path);
    }
}
