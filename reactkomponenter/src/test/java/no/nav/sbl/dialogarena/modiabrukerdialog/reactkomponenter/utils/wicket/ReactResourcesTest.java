package no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket;

import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactResources;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class ReactResourcesTest {

    private final List<String> forventedeJSFiler = Collections.singletonList("build/reactkomponenter.js");
    private final List<String> forventedeLessFiler = Arrays.asList(
            "build/modal.less",
            "build/redirect-modal.less",
            "build/sok-layout.less",
            "build/meldinger-sok.less",
            "build/knagginput.less",
            "build/skrivestotte.less",
            "build/journalforing-panel.less",
            "build/varsel-module.less",
            "build/saksoversikt-module.less",
            "build/pleiepenger-panel.less",
            "build/delvis-svar.less",
            "build/traadvisning.less",
            "build/nav-core-variabler.less",
            "build/nav-kontor.less",
            "build/slaa-sammen-traader.less",
            "build/alertstripe.less",
            "build/tildelt-flere-oppgaver-alert.less",
            "build/ny-frontend.less"
    );

    @Test
    public void frontModuleInneholderAlleForventedeJSFiler() throws Exception {
        assertTrue("Forventet JS-ressurs mangler i listen.", Arrays.stream(ReactResources.REACT_KOMPONENTER.getScripts()).
                map(PackageResourceReference::getName).collect(Collectors.toList()).containsAll(forventedeJSFiler));
    }

    @Test
    public void frontModuleInneholderAlleForventedeLESSFiler() throws Exception {
        assertTrue("Forventet LESS-ressurs mangler i listen.", Arrays.stream(ReactResources.REACT_KOMPONENTER.getLess()).
                map(PackageResourceReference::getName).collect(Collectors.toList()).containsAll(forventedeLessFiler));
    }

    @Test
    public void frontModuleInneholderKunForventedeJSFiler() throws Exception {
        assertEmpty("Listen inneholder en JS-ressurs som ikke er forventet",
                Arrays.stream(ReactResources.REACT_KOMPONENTER.getScripts()).
                        filter(resource -> !forventedeJSFiler.contains(resource.getName())).collect(Collectors.toList()));
    }

    @Test
    public void frontModuleInneholderKunForventedeLESSFiler() throws Exception {
        assertEmpty("Listen inneholder en LESS-ressurs som ikke er forventet",
                Arrays.stream(ReactResources.REACT_KOMPONENTER.getLess()).
                        filter(resource -> !forventedeLessFiler.contains(resource.getName())).collect(Collectors.toList()));
    }

    private void assertEmpty(final String message, final Collection collection) {
        assertTrue(message, collection.isEmpty());
    }

}