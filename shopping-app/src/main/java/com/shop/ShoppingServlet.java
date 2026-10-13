
package com.shop;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@WebServlet("/")
public class ShoppingServlet extends HttpServlet {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private String getContainerHostname() {
        String hostname = System.getenv("HOSTNAME");

        if (hostname != null && !hostname.isBlank()) {
            return hostname;
        }

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "hostname-unavailable";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        String shoppingHost = getContainerHostname();

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("X-Shopping-Container", shoppingHost);
        response.setHeader("Cache-Control", "no-store");

        String productDetails = "";
        String productHost = "";
        String error = "";

        if ("true".equals(request.getParameter("details"))) {
            String apiUrl = System.getenv().getOrDefault(
                    "PRODUCT_API_URL",
                    "http://product-service:8080/api/product"
            );

            try {
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
                    productHost = apiResponse.headers()
                            .firstValue("X-Container-Hostname")
                            .orElse("Hostname header missing");
                } else {
                    error = "Product API returned HTTP "
                            + apiResponse.statusCode();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                error = "Product request interrupted.";

            } catch (Exception e) {
                error = "Product API error: "
                        + e.getClass().getSimpleName();
                getServletContext().log("Product API failed", e);
            }
        }

        response.getWriter().println(
                "<!DOCTYPE html><html><head>"
                + "<title>Online Shopping</title>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>"
                + "body{font-family:Arial;background:#f1f5f9;margin:0;color:#1e293b}"
                + ".header{background:#0f172a;color:white;padding:22px;text-align:center;font-size:28px;font-weight:bold}"
                + ".card{background:white;max-width:700px;margin:45px auto;padding:30px 25px;text-align:center;border-radius:12px;box-shadow:0 5px 20px #0001}"
                + ".host{background:#dcfce7;padding:15px;margin:15px 0;border-radius:8px;overflow-wrap:anywhere}"
                + ".details{background:#eff6ff;padding:20px;margin-top:25px;border-radius:8px;text-align:left;overflow-wrap:anywhere}"
                + "button{background:#2563eb;color:white;border:0;padding:14px 25px;border-radius:7px;font-size:17px;cursor:pointer}"
                + "pre{white-space:pre-wrap;overflow-wrap:anywhere;font-size:16px}"
                + ".error{color:#dc2626}"
                + "</style></head><body>"

                + "<div class='header'>Online Shopping</div>"
                + "<div class='card'><h1>Container Identification</h1>"

                + "<div class='host'><b>Shopping App container serving this page</b><br>"
                + escapeHtml(shoppingHost) + "</div>"

                + "<p>Click to identify the Product Service container.</p>"
                + "<form method='GET' action='/'>"
                + "<button type='submit' name='details' value='true'>"
                + "Get Product Details</button></form>"

                + (!productDetails.isEmpty()
                    ? "<div class='details'><h2>Product API Response</h2>"
                    + "<p><b>Product Service container that handled this API request:</b><br>"
                    + escapeHtml(productHost) + "</p>"
                    + "<pre>" + escapeHtml(productDetails) + "</pre></div>"
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

