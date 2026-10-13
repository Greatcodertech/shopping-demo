
package com.shop;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetAddress;

@WebServlet("/api/product")
public class ProductServlet extends HttpServlet {

    private String containerName() {
        String name = System.getenv("HOSTNAME");
        if (name != null && !name.isBlank()) {
            return name;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        String hostname = containerName();

        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("X-Container-Hostname", hostname);
        response.setHeader("Cache-Control", "no-store");

        getServletContext().log(
                "Product request handled by container: " + hostname
        );

        response.getWriter().println("Product: Laptop");
        response.getWriter().println("Price: Rs. 50000");
        response.getWriter().println("Availability: In Stock");
    }
}

