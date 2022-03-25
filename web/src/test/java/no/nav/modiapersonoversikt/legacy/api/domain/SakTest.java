package no.nav.modiapersonoversikt.legacy.api.domain;

import no.nav.modiapersonoversikt.service.saker.Sak;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class SakTest {

    private static final String FAGSYSTEM_SAKS_ID = "fagsystemSaksId";
    private static final String SAKS_ID = "saksId";
    private static final String TEMAKODE = "temakode";
    private static final String FAGSYSTEMKODE = "fagsystemkode";
    private static final String SAKSTYPE = "sakstype";

    @Test
    public void returnererFagsystemSaksIdDersomDenneErSatt() {
        Sak sak = new Sak();
        sak.fagsystemSaksId = FAGSYSTEM_SAKS_ID;
        sak.saksId = SAKS_ID;

        assertThat(sak.getSaksIdVisning(), is(FAGSYSTEM_SAKS_ID));
    }

    @Test
    public void returnererSaksIdDersomDenneErSattOgIkkeFagsystemSaksIdErSatt() {
        Sak sak = new Sak();
        sak.saksId = SAKS_ID;

        assertThat(sak.getSaksIdVisning(), is(SAKS_ID));
    }

    @Test
    public void returnererTomStrengDersomFagsystemSaksIdOgSaksIdIkkeErSatt() {
        assertThat(new Sak().getSaksIdVisning(), is(""));
    }

    @Test
    public void sammenlignerSakerBaertPaaSaksIdDersomDenneErSatt() {
        Sak sak1 = new Sak();
        sak1.saksId = SAKS_ID;

        Sak sak2 = new Sak();
        sak2.saksId = SAKS_ID;

        Sak sak3 = new Sak();

        assertThat(sak1.equals(sak2), is(true));
        assertThat(sak1.hashCode(), is(sak2.hashCode()));

        assertThat(sak1.equals(sak3), is(false));
        assertThat(sak1.hashCode(), is(not(sak3.hashCode())));
    }

    @Test
    public void sammenlignerSakerBaertPaaAndreFelterDersomSaksIdIkkeErSatt() {
        Sak sak1 = new Sak();
        sak1.temaKode = TEMAKODE;
        sak1.fagsystemKode = FAGSYSTEMKODE;
        sak1.sakstype = SAKSTYPE;

        Sak sak2 = new Sak();
        sak2.temaKode = TEMAKODE;
        sak2.fagsystemKode = FAGSYSTEMKODE;
        sak2.sakstype = SAKSTYPE;

        Sak sak3 = new Sak();
        sak3.temaKode = "Annen temakode";
        sak3.fagsystemKode = FAGSYSTEMKODE;
        sak3.sakstype = SAKSTYPE;

        Sak sak4 = new Sak();
        sak4.temaKode = TEMAKODE;
        sak4.fagsystemKode = "Annen fagsystemkode";
        sak4.sakstype = SAKSTYPE;

        Sak sak5 = new Sak();
        sak5.temaKode = TEMAKODE;
        sak5.fagsystemKode = FAGSYSTEMKODE;
        sak5.sakstype = "Annen sakstype";

        assertThat(sak1.equals(sak2), is(true));
        assertThat(sak1.hashCode(), is(sak2.hashCode()));

        assertThat(sak1.equals(sak3), is(false));
        assertThat(sak1.hashCode(), is(not(sak3.hashCode())));

        assertThat(sak1.equals(sak4), is(false));
        assertThat(sak1.hashCode(), is(not(sak4.hashCode())));

        assertThat(sak1.equals(sak5), is(false));
        assertThat(sak1.hashCode(), is(not(sak5.hashCode())));
    }
}
