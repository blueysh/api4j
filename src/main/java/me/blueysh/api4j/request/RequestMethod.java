package me.blueysh.api4j.request;

@SuppressWarnings("unused")
public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    OPTIONS("OPTIONS"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD");

    private final String methodName;

    RequestMethod(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public static RequestMethod of(String s) {
        return switch (s.toLowerCase()) {
            case "get" -> GET;
            case "post" -> POST;
            default -> null;
        };
    }
}
