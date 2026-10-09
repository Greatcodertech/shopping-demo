package com.shop;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/product")
public class ProductServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String product = "Product: Laptop"
                + "\nPrice: Rs. 50000"
                + "\nAvailability: In Stock";

        System.out.println("Product API called by Shopping App");
        response.getWriter().write(product);
    }
}
