# `api4j`
> ## ✅ A simple Java library for building Web APIs.

**This is an in-progress project! PRs and issues are highly encouraged!** I am currently working on adding more features.

## Usage
api4j is very easy to get started with.

### Dependency
To use api4j, you need to add the dependency to your project.

Maven:
```xml
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>

  <dependency>
      <groupId>com.github.blueysh</groupId>
      <artifactId>api4j</artifactId>
      <version>VERSION</version>
  </dependency>
```

Gradle:
```gradle
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }

  dependencies {
    implementation 'com.github.blueysh:api4j:VERSION'
  }
```

### Create your server
Setting up an API server is simple.

Create an instance of the APIServer class like this:
```java
// Listens at 0.0.0.0 on port 8080
APIServer myServer = APIServer.create(new InetSocketAddress("0.0.0.0", 8080));
```

You can also specify backlog and thread values:
```java
// Listens at 0.0.0.0 on port 8080.
// Backlog is set to 0, and number of threads is set to 2.
APIServer myServer = APIServer.create(new InetSocketAddress("0.0.0.0", 8080), 0, 2);
```

### Creating Endpoints
Endpoints in api4j are classes that extend `Endpoint`, and have handler methods depending on the methods you allow your endpoint to receive.
Endpoint classes must be annotated with `EndpointData`.

Here is an example of a simple endpoint that allows GET requests:
```java
import me.blueysh.api4j.handler.GetHandler;
import me.blueysh.api4j.request.RequestMethod;
import me.blueysh.api4j.endpoint.Endpoint;
import me.blueysh.api4j.endpoint.EndpointData;

// This makes our Endpoint accessible at "/my-endpoint" through GET requests only.
@EndpointData(path = "/my-endpoint", description = "", allowedMethods = {RequestMethod.GET})
public class MyEndpoint extends Endpoint {

  // Handles our GET requests.
  @GetHandler
  public void handleGet(Request request) {
    try {
      // Fulfills the request with status code 200 and the specified content.
      request.fulfill(new Response(ResponseCode.OK, "You've just sent a GET request! Cool!"));
    } catch (RequestAlreadyFulfilledException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  // One method that handles all requests with RequestMethods that are not allowed.
  @Override
  public void handleInvalidMethod(@NotNull Request request) {
    try {
        request.fulfill(new Response(ResponseCode.METHOD_NOT_ALLOWED, "Cannot " + request.getMethod().getMethodName() + " to " + getPath()));
    } catch (RequestAlreadyFulfilledException | IOException e) {
        throw new RuntimeException(e);
    }
  }
}
```

**An exception will be thrown if a request is received for a RequestMethod that has no corresponding Handler method.*

We can register our Endpoint to our server like so:
```java
myServer.registerEndpoint(new MyEndpoint());
```

### Request Content
```java
request.getContent();  // Gets String containing request body
request.getMethod();   // Gets RequestMethod used when sending the request
request.getHeaders();  // Gets the Headers sent in the request
request.getUri();      // Gets the URI of the request
request.getExchange(); // Gets HTTPExchange of the request
request.isFulfilled(); // Gets boolean of the fulfillment status
```

### Starting the Server
Finally, we can start our server and begin receiving requests!

```java
myServer.start();
```
