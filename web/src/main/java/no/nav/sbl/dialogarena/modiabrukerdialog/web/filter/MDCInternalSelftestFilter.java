package no.nav.sbl.dialogarena.modiabrukerdialog.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.nav.modig.presentation.logging.session.MDCFilter;


public class MDCInternalSelftestFilter extends MDCFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain) throws ServletException, IOException {
        if (httpServletRequest.getRequestURI().contains("/internal/selftest")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            super.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        }
    }

}
