package ru.netology.servlet;

public enum HTTPMethods {
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    CONNECT("CONNECT"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    PATCH("PATCH");
    private final String title;

    HTTPMethods(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
