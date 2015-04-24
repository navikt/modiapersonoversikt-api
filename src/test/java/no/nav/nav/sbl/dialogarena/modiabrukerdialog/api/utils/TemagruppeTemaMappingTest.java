package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import org.junit.Test;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TemagruppeTemaMappingTest {

    @Test
    public void temaUtenTemagruppeSkalReturnereDefault() {
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("NOE SOM IKKE FINNES"), is(TemagruppeTemaMapping.TEMA_UTEN_TEMAGRUPPE.name()));
    }

    @Test
    public void temaTilTemagruppeTest() {
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("AAP"), is(ARBD.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("FOS"), is(ARBD.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("HEL"), is(HJLPM.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("TRK"), is(OVRG.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("UFO"), is(PENS.name()));
    }
}