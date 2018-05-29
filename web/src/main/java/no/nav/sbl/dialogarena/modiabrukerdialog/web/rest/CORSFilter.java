package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggleKt.visFeature;

public class CORSFilter implements Filter {

    private static List<String> allowedOrigins = Arrays.asList(
            ".adeo.no",
            ".nais.preprod.local",
            "http://localhost:3000"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String origin = httpRequest.getHeader("Origin");
        if (visFeature(Feature.PERSON_REST_API)) {
            setCorsHeadere(httpResponse, origin);
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }

    private void setCorsHeadere(HttpServletResponse httpResponse, String origin) {
        if (origin != null && allowedOrigins.stream().anyMatch(origin::endsWith)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, NAV_CSRF_PROTECTION");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        }
    }

    @Override
    public void destroy() {

    }
}
