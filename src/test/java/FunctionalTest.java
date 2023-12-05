import me.blueysh.api4j.APIServer;
import me.blueysh.api4j.endpoint.handler.GetHandler;
import me.blueysh.api4j.endpoint.handler.PostHandler;
import me.blueysh.api4j.request.RequestMethod;
import me.blueysh.api4j.endpoint.Endpoint;
import me.blueysh.api4j.endpoint.EndpointData;
import me.blueysh.api4j.except.endpoint.EndpointAlreadyExistsException;
import me.blueysh.api4j.except.request.RequestAlreadyFulfilledException;
import me.blueysh.api4j.request.Request;
import me.blueysh.api4j.request.Response;
import me.blueysh.api4j.request.ResponseCode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FunctionalTest {
    public static void main(String[] args) throws IOException, EndpointAlreadyExistsException {
        // Creates an APIServer
        APIServer server = APIServer.create(new InetSocketAddress("0.0.0.0", 1234));

        // Registers our endpoint
        server.registerEndpoint(new RootEndpoint());

        // Starts our server
        server.start();

        System.out.println("server is running.");
    }

    /*
     * This is our Endpoint.
     *
     * It requires the @EndpointData annotation.
     */
    @EndpointData(path = "/", description = "This is the root endpoint.", allowedMethods = {RequestMethod.GET, RequestMethod.POST})
    private static class RootEndpoint extends Endpoint {
        /*
         * This is our handler for GET requests.
         *
         * This method can be named anything as long as it is annotated with @GetHandler.
         */
        @GetHandler
        public void handleGet(Request request) {
            try {
                request.fulfill(new Response(ResponseCode.OK, "You've just sent a GET request! Cool!"));
            } catch (RequestAlreadyFulfilledException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        /*
         * This is our handler for POST requests.
         *
         * This method can be named anything as long as it is annotated with @PostHandler.
         */
        @PostHandler
        public void handlePost(Request request) {
            try {
                request.fulfill(new Response(ResponseCode.CREATED, "You've just sent a POST request! This is what you sent: " + request.getContent()));
            } catch (RequestAlreadyFulfilledException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void handleInvalidMethod(@NotNull Request request) {
            try {
                request.fulfill(new Response(ResponseCode.METHOD_NOT_ALLOWED, "Cannot " + request.getMethod().getMethodName() + " to " + getPath()));
            } catch (RequestAlreadyFulfilledException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
