package com.shop;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@WebServlet("/")
public class ShoppingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        String apiUrl = System.getenv().getOrDefault(
            "PRODUCT_API_URL",
            "http://product-service:8080/api/product"
        );

        String result;
        int status = 200;

        try {
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

            HttpRequest apiRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

            HttpResponse<String> apiResponse = client.send(
                apiRequest,
                HttpResponse.BodyHandlers.ofString()
            );

            if (apiResponse.statusCode() != 200) {
                throw new IOException(
                    "Product API returned HTTP "
                    + apiResponse.statusCode()
                );
            }

            result = apiResponse.body();

            System.out.println("Received product data from Container 2:");
            System.out.println(result);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            status = 503;
            result = "API request interrupted.";
        } catch (Exception e) {
            status = 502;
            result = "Could not connect to Product Service.";
            getServletContext().log("Product API request failed", e);
        }

        response.setStatus(status);

        response.getWriter().println(
            "<html><head><title>Shopping App</title></head><body>"
            + "<h1>Online Shopping</h1>"
            + "<h2>Product Details Received from Container 2</h2>"
            + "<pre>" + escapeHtml(result) + "</pre>"
            + "</body></html>"
        );
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }
}
