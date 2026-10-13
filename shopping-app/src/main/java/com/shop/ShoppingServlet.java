package com.shop;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.InetAddress;
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

        String containerHost;
        try {
            containerHost = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            containerHost = "hostname-unavailable";
        }

        String productDetails = "";
        String error = "";

        // Fetch product details only after button click
        if ("true".equals(request.getParameter("details"))) {

            String apiUrl = System.getenv().getOrDefault(
                "PRODUCT_API_URL",
                "http://product-service:8080/api/product"
            );

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

                if (apiResponse.statusCode() == 200) {
                    productDetails = apiResponse.body();
                } else {
                    error = "Product API returned HTTP "
                            + apiResponse.statusCode();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                error = "Product request was interrupted.";

            } catch (Exception e) {
                error = "Unable to connect to Product Service.";
                getServletContext().log("Product API failed", e);
            }
        }

        response.getWriter().println(
            "<!DOCTYPE html>"
            + "<html><head><title>Online Shopping</title>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1'>"

            + "<style>"
            + "body{font-family:Arial;background:#f1f5f9;margin:0;color:#1e293b}"
            + ".header{background:#0f172a;color:white;padding:22px;text-align:center;font-size:28px;font-weight:bold}"
            + ".card{background:white;max-width:650px;margin:70px auto;padding:35px 25px;text-align:center;border-radius:12px;box-shadow:0 5px 20px #0001}"
            + "button{background:#2563eb;color:white;border:0;padding:14px 25px;border-radius:7px;font-size:17px;cursor:pointer}"
            + "button:hover{background:#1d4ed8}"
            + ".details{background:#eff6ff;padding:20px;margin-top:25px;border-radius:8px;text-align:left;overflow-wrap:anywhere}"
            + "pre{white-space:pre-wrap;overflow-wrap:anywhere;font-size:16px}"
            + ".error{color:#dc2626;margin-top:20px}"
            + "</style></head><body>"

            + "<div class='header'>Online Shopping</div>"

            + "<div class='card'>"
            + "<h1>Product Details</h1>"
            + "<p>Shopping container hostname: <b>" + escapeHtml(containerHost) + "</b></p>"
            + "<p>Click below to receive product information from the product service.</p>"

            + "<form method='GET' action='/'>"
            + "<button type='submit' name='details' value='true'>"
            + "Product Details"
            + "</button>"
            + "</form>"

            // Display information only after the click
            + (!productDetails.isEmpty()
                ? "<div class='details'>"
                    + "<h2>Product Details Received from Container 2</h2>"
                    + "<pre>" + escapeHtml(productDetails) + "</pre>"
                    + "</div>"
                : "")

            + (!error.isEmpty()
                ? "<p class='error'>" + escapeHtml(error) + "</p>"
                : "")

            + "</div></body></html>"
        );
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
