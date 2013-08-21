package no.nav.sbl.dialogarena.sporsmalogsvar.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class Brukerindeks extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        try (InputStream htmlFileStream = Brukerindeks.class.getResourceAsStream("/brukere.html")) {
            IOUtils.copy(htmlFileStream, resp.getOutputStream());
        }
    }
}
