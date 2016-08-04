package no.nav.sbl.dialogarena.varsel.lamell;

import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class VarslerOversiktLinkTest {

    private Optional<List<Varsel>> varsler;

    @Test
    public void nyeVarslerSkalReturnereRiktigCmsKey() {
        List<Varsel> varselListe = dummyVarsel();
        varsler = of(varselListe);

        assertThat(VarslerOversiktLink.hentCMSKeyForVarselLenke(1, varsler), is("varsler.oversikt.lenke.nye.varsler"));
    }

    @Test
    public void ingenNyeVarslerSkalReturnereRiktigCmsKey() {
        List<Varsel> varselListe = dummyVarsel();
        varsler = of(varselListe);

        assertThat(VarslerOversiktLink.hentCMSKeyForVarselLenke(0, varsler), is("varsler.oversikt.lenke"));
    }

    @Test
    public void ingenVarslerSkalReturnereRiktigCmsKey() {
        assertThat(VarslerOversiktLink.hentCMSKeyForVarselLenke(0, of(Collections.<Varsel>emptyList())), is("varsler.oversikt.lenke.ingen.varsler"));
    }

    @Test
    public void feilVedUthentingAvVarslerSkalReturnereRiktigCmsKey() {
        varsler = empty();
        assertThat(VarslerOversiktLink.hentCMSKeyForVarselLenke(1, varsler), is("varsler.oversikt.lenke.feil.uthenting"));
    }

    private List<Varsel> dummyVarsel() {
        List<Varsel> varselListe = new ArrayList<>();
        varselListe.add(mock(Varsel.class));
        return varselListe;
    }
}