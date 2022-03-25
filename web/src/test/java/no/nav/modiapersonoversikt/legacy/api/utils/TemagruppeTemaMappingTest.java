package no.nav.modiapersonoversikt.legacy.api.utils;


import no.nav.modiapersonoversikt.commondomain.TemagruppeTemaMapping;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.modiapersonoversikt.commondomain.Temagruppe.*;
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
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("HJE"), is(HJLPM.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("HEL"), is(ORT_HJE.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("TRK"), is(OVRG.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("SUP"), is(PENS.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("UFO"), is(UFRT.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("TSO"), is(ARBD.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("TSR"), is(ARBD.name()));
        assertThat(TemagruppeTemaMapping.hentTemagruppeForTema("OMS"), is(FMLI.name()));
    }

    @Test
    public void temaGruppeTilTemaTestUFRTSkalMappeTilUFO() {
        List<String> temaer = TemagruppeTemaMapping.hentTemaForTemagruppe("UFRT");
        assertThat(temaer.contains("UFO"), is(true));
    }

    @Test
    public void temaGruppeTilTemaTestUTLANDSkalMappeTilNoe() {
        List<String> temaer = TemagruppeTemaMapping.hentTemaForTemagruppe("UTLAND");

        assertThat(temaer.contains("OMS"), is(true));
        assertThat(temaer.contains("SUP"), is(true));
    }

}
