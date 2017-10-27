/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi.web;

import java.io.IOException;
import javax.annotation.security.DeclareRoles;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author m28dev
 */
@WebServlet("/servlet")
@DeclareRoles({"foo", "bar", "kaz"})
@ServletSecurity(
        @HttpConstraint(rolesAllowed = "foo")
)
public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html; charset=utf-8");
        response.getWriter().write("<!DOCTYPE html>");
        response.getWriter().write("<title>learn security api</title>");

        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }

        response.getWriter().write("<p>");
        response.getWriter().write("web username: " + webName);
        response.getWriter().write("</p>");

        response.getWriter().write("<ul>");
        response.getWriter().write("<li>" + "web user has role \"foo\": " + request.isUserInRole("foo") + "</li>");
        response.getWriter().write("<li>" + "web user has role \"bar\": " + request.isUserInRole("bar") + "</li>");
        response.getWriter().write("<li>" + "web user has role \"kaz\": " + request.isUserInRole("kaz") + "</li>");
        response.getWriter().write("</ul>");

    }

}
