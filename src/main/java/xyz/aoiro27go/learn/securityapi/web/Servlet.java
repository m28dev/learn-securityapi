/*
 * The MIT License
 *
 * Copyright 2017 m28dev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
