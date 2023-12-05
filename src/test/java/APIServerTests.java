import me.blueysh.api4j.APIServer;
import me.blueysh.api4j.request.RequestMethod;
import me.blueysh.api4j.endpoint.Endpoint;
import me.blueysh.api4j.endpoint.EndpointData;
import me.blueysh.api4j.except.endpoint.EndpointAlreadyExistsException;
import me.blueysh.api4j.request.Request;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class APIServerTests {
    private APIServer server;

    void startServer() throws IOException {
        if (server == null) {
            server = APIServer.create(new InetSocketAddress("0.0.0.0", 1234));
            server.start();
        }
    }

    void killServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Test
    public void assertIsRunningAfterStart() throws IOException {
        startServer();
        assertTrue(server.isRunning());
        killServer();
    }

    @Test
    public void assertIsNotRunningAfterStop() throws IOException {
        startServer();
        server.stop();
        assertFalse(server.isRunning());
        killServer();
    }

    @Test
    public void assertAddrNotNull() throws IOException {
        startServer();
        assertNotNull(server.getAddr());
        killServer();
    }

    @Test
    public void assertEndpointNotNull() throws IOException, EndpointAlreadyExistsException {
        startServer();

        @EndpointData(path = "/test", description = "", allowedMethods = {RequestMethod.GET})
        class TestEndpoint extends Endpoint {
            @Override
            public void handleInvalidMethod(@NotNull Request request) {

            }
        }

        server.registerEndpoint(new TestEndpoint());

        assertNotNull(server.getEndpointByPath("/test"));

        killServer();
    }

    @Test
    public void assertEndpointIsNull() throws IOException, EndpointAlreadyExistsException {
        startServer();

        @EndpointData(path = "/test", description = "", allowedMethods = {RequestMethod.GET})
        class TestEndpoint extends Endpoint {
            @Override
            public void handleInvalidMethod(@NotNull Request request) {

            }
        }

        server.registerEndpoint(new TestEndpoint());

        server.unregisterEndpointByPath("/test");

        assertNull(server.getEndpointByPath("/test"));

        killServer();
    }
}
