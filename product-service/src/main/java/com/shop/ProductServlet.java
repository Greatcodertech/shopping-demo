
package com.shop;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetAddress;

@WebServlet("/api/product")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain;charset=UTF-8");

        // Identify the Product Service container
        String productHost = System.getenv("HOSTNAME");

        if (productHost == null || productHost.isBlank()) {
            try {
                productHost = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                productHost = "hostname-unavailable";
            }
        }

        // Send container hostname to Shopping App
        response.setHeader("X-Container-Hostname", productHost);

        // Log which container handled the request
        getServletContext().log(
                "Product API called by container: " + productHost
        );

        // Return product information
        response.getWriter().println("Product: Laptop");
        response.getWriter().println("Price: Rs. 50000");
        response.getWriter().println("Availability: In Stock");
    }
}

