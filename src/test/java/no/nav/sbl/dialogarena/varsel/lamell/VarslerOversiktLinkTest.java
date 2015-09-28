package no.nav.sbl.dialogarena.varsel.lamell;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VarslerOversiktLinkTest {

    @Test
    public void nyeVarslerSkalReturnereRiktigCmsKey() {
        assertThat(VarslerOversiktLink.hentCMSKeyForVarselLenke(1, true), is("varsler.oversikt.lenke.nye.varsler"));
    }

    @Test
    public void ingenNyeVarslerSkalReturnereRiktigCmsKey() {
        assertThat(VarslerOversiktLink.hentCMSKeyForVarselLenke(0, true), is("varsler.oversikt.lenke"));
    }

    @Test
    public void ingenVarslerSkalReturnereRiktigCmsKey() {
        assertThat(VarslerOversiktLink.hentCMSKeyForVarselLenke(0, false), is("varsler.oversikt.lenke.ingen.varsler"));
    }
}