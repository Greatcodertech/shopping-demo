
package com.shop;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetAddress;

@WebServlet("/api/product")
public class ProductServlet extends HttpServlet {

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

        String productHost = getContainerHostname();

        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("X-Container-Hostname", productHost);
        response.setHeader("Cache-Control", "no-store");

        getServletContext().log(
                "Product API request handled by container: " + productHost
        );

        response.getWriter().println("Product: Laptop");
        response.getWriter().println("Price: Rs. 50000");
        response.getWriter().println("Availability: In Stock");
    }
}

