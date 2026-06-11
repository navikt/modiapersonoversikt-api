package no.nav.modiapersonoversikt.service.journalforingsaker;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class JournalforingSakTest {

    private static final String TEMAKODE = "temakode";
    private static final String FAGSYSTEMKODE = "fagsystemkode";
    private static final String SAKSTYPE = "sakstype";

    @Test
    public void sammenlignerSaker() {
        JournalforingSak sak1 = new JournalforingSak();
        sak1.temaKode = TEMAKODE;
        sak1.fagsystemKode = FAGSYSTEMKODE;
        sak1.sakstype = SAKSTYPE;

        JournalforingSak sak2 = new JournalforingSak();
        sak2.temaKode = TEMAKODE;
        sak2.fagsystemKode = FAGSYSTEMKODE;
        sak2.sakstype = SAKSTYPE;

        JournalforingSak sak3 = new JournalforingSak();
        sak3.temaKode = "Annen temakode";
        sak3.fagsystemKode = FAGSYSTEMKODE;
        sak3.sakstype = SAKSTYPE;

        JournalforingSak sak4 = new JournalforingSak();
        sak4.temaKode = TEMAKODE;
        sak4.fagsystemKode = "Annen fagsystemkode";
        sak4.sakstype = SAKSTYPE;

        JournalforingSak sak5 = new JournalforingSak();
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

