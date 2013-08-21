package no.nav.sbl.dialogarena.sporsmalogsvar.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import no.nav.modig.core.context.SubjectHandlerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityFilterMock implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityFilterMock.class);

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.warn("Aktivert " + getClass().getSimpleName() + "! Skal ikke opptre i produksjon!");
    }

    // Checkstyle tror det er redundante Exceptions
    // CHECKSTYLE:OFF
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (req.getRequestURI().matches("^(.*internal/selftest.*)|(.*index.html)|(.*feil.*)|((.*)\\.(js|css|jpg))")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (req.getParameter("fnr") != null) {
            req.getSession().setAttribute("fnr", req.getParameter("fnr"));
        }
        String fnr = (String) req.getSession().getAttribute("fnr");
        if (fnr == null) {
            throw new RuntimeException("Du må sende med ?fnr=xxxx for å logge på");
        }

        SubjectHandlerUtils.setEksternBruker(fnr, 4, null);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

}
