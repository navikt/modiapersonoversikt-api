package no.nav.modiapersonoversikt.legacy.api.domain.henvendelse;

import no.nav.modiapersonoversikt.legacy.api.domain.Person;
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TraadTest {

    @Test
    public void traaderSkalSorteresMedNyesteMeldingForst() {
        final String traadId = "traadId";
        final Traad traad = new Traad(traadId, 2,
                Arrays.asList(
                        lagMelding(traadId, "melding_1", 1),
                        lagMelding(traadId, "melding_2", 2),
                        lagMelding(traadId, "melding_3", 3)
                ));

        assertThat(traad.meldinger.get(0).id, is("melding_3"));
    }

    private Melding lagMelding(final String traadId, final String id, final long dato) {
        return new Melding()
                .withId(id)
                .withFerdigstiltDato(new DateTime(dato))
                .withBrukersEnhet("brukersEnhet")
                .withEksternAktor("eksternAktor")
                .withErTilknyttetAnsatt(false)
                .withFnr("10108000398")
                .withFritekst(new Fritekst("fritekst", new Person("", ""), new DateTime(dato)))
                .withGjeldendeTemagruppe(Temagruppe.ANSOS)
                .withJournalfortAvNavIdent("Z990335")
                .withJournalfortDato(new DateTime(dato))
                .withJournalfortTema("Dagpenger")
                .withJournalfortTemaNavn("DAG")
                .withTraadId(traadId)
                .withOppgaveId("oppgaveId")
                .withType(Meldingstype.OPPGAVE_VARSEL)
                .withTemagruppe("temaGruppe")
                .withKanal("kanal")
                .withKontorsperretEnhet("2820")
                .withJournalfortSaksId("journalfortSaksId")
                .withTilknyttetEnhet("0216")
                .withErFerdigstiltUtenSvar(false);
    }

}
