package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.core.context.*;
import no.nav.modig.core.domain.IdentType;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok.DEFAULT_TIME_TO_LIVE_MINUTES;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok.LUCENE_PATTERN;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok.REPLACEMENT_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTimeUtils.setCurrentMillisOffset;

public class MeldingerSokTest {

    private static final String NAV_IDENT = "Z999999";
    private static final String FNR = "11111111111";
    private MeldingerSok meldingerSok = new MeldingerSok();

    @Before
    public void setup() {
        innloggetBrukerEr(NAV_IDENT);
        meldingerSok.indekser(FNR, lagMeldinger());
    }

    @Test
    public void returnererAlleMeldingerVedTomtSok() {
        List<Melding> meldinger = alleMeldinger(meldingerSok.sok(FNR, ""));
        assertThat(meldinger, hasSize(lagMeldinger().size()));
    }

    @Test
    public void sletterCacheEtterEnGittStund() {
        assertThat(meldingerSok.cache.entrySet(), hasSize(1));

        setCurrentMillisOffset(Integer.valueOf(DEFAULT_TIME_TO_LIVE_MINUTES) * 60 * 1000);
        meldingerSok.ryddOppCache();

        assertThat(meldingerSok.cache.entrySet(), hasSize(0));
    }

    @Test
    public void fritekstTemagruppeKanalStatusTekstOgArkivtemaErSokbart() {
        assertSok("rbei", "1235");
        assertSok("uføre", "1236");
        assertSok("svar", "1234");
        assertSok("skriftlig", "1235");
    }

    @Test
    public void returnererMeldingerSortertEtterDato() {
        String fnr = "987654321";
        List<Melding> meldinger = asList(
                lagMelding("1", "1", "", "", "", DateTime.now().minusDays(2), "", ""),
                lagMelding("2", "2", "", "", "", DateTime.now().minusDays(1), "", ""),
                lagMelding("3", "3", "", "", "", DateTime.now(), "", ""),
                lagMelding("4", "4", "", "", "", DateTime.now().minusDays(3), "", ""));
        meldingerSok.indekser(fnr, meldinger);

        List<Traad> traader = meldingerSok.sok(fnr, "");
        assertThat(traader.get(0).meldinger.get(0).id, is("3"));
        assertThat(traader.get(1).meldinger.get(0).id, is("2"));
        assertThat(traader.get(2).meldinger.get(0).id, is("1"));
        assertThat(traader.get(3).meldinger.get(0).id, is("4"));
    }

    @Test
    public void gruppererMeldingerISammeTraad() {
        String fnr = "4561234789";
        DateTime now = DateTime.now();
        List<Melding> meldinger = asList(
                lagMelding("1", "1", "", "", "", now.minusDays(2), "", ""),
                lagMelding("2", "1", "", "", "", now.minusDays(1), "", ""),
                lagMelding("3", "2", "", "", "", now, "", ""),
                lagMelding("4", "4", "", "", "", now.minusDays(3), "", ""));
        meldingerSok.indekser(fnr, meldinger);

        List<Traad> traader = meldingerSok.sok(fnr, "");
        assertThat(traader, hasSize(3));
        assertThat(traader.get(0).dato, is(now));
        assertThat(traader.get(1).dato, is(now.minusDays(1)));
        assertThat(traader.get(2).dato, is(now.minusDays(3)));
    }

    @Test
    public void forskjelligeSaksbehandlereFaarIkkeSammeResultat() {
        innloggetBrukerEr("Z132456");
        meldingerSok.indekser(FNR, Collections.<Melding>emptyList());

        assertThat(meldingerSok.sok(FNR, ""), hasSize(0));
        assertThat(meldingerSok.cache.entrySet(), hasSize(2));

        assertThat(meldingerSok.sok(FNR, ""), hasSize(0));
    }

    @Test
    public void returnererTraaderMedAntallMeldingerIOpprinneligTraad() {
        List<Traad> pernsjonsTraader = meldingerSok.sok(FNR, "Hjelpemidler");

        assertThat(pernsjonsTraader.size(), is(1));
        assertThat(pernsjonsTraader.get(0).antallMeldingerIOpprinneligTraad, is(2));

        List<Traad> familieTraader = meldingerSok.sok(FNR, "Familie");

        assertThat(familieTraader.size(), is(1));
        assertThat(familieTraader.get(0).antallMeldingerIOpprinneligTraad, is(1));
    }

    @Test
    public void patternetTrefferOgVaskerSpesialtegn() {
        String soketekst = "\\+!():^[]{}~?=/|.\"";
        String vasketSoketekst = LUCENE_PATTERN.matcher(soketekst).replaceAll(REPLACEMENT_STRING);

        assertThat(vasketSoketekst, is(""));
    }

    private void innloggetBrukerEr(String ident) {
        setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        setProperty(ModigSecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(ident, IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

    private void assertSok(String frisok, String id) {
        List<Melding> fritekst = alleMeldinger(meldingerSok.sok(FNR, frisok));
        assertThat(fritekst, hasSize(1));
        assertThat(fritekst.get(0).id, is(id));
    }

    private static List<Melding> lagMeldinger() {
        return asList(
                lagMelding("1234", "tekst 1 tekst 1 tekst 1", "Familie", "", "svar", "telefon"),
                lagMelding("1235", "tekst 2 tekst 2 tekst 2", "Arbeid", "Dagpenger", "spørsmål", "skriftlig"),
                lagMelding("1236", "tekst 3 tekst 3 tekst 3", "Pensjon", "Uførepensjon", "referat", ""),
                lagMelding("1237", "tekst 3 tekst 3 tekst 3", "Hjelpemidler", "Hjelpemiddel", "referat", ""),
                lagMelding("1237", "yyy yyy yyy", "yyyy", "yyyyy", "yyyy", ""));
    }

    private static Melding lagMelding(String behandlingsId, String fritekst, String temagruppe, String arkivtema, String statustekst, String kanal) {
        return lagMelding(behandlingsId, behandlingsId, fritekst, temagruppe, arkivtema, DateTime.now(), statustekst, kanal);
    }

    private static Melding lagMelding(String behandlingsId, String behandlingskjedeId, String fritekst, String temagruppe,
                                      String arkivtema, DateTime dato, String statustekst, String kanal) {
        Melding melding = new Melding(behandlingsId, SAMTALEREFERAT_OPPMOTE, dato);
        melding.traadId = behandlingskjedeId;
        melding.fritekst = fritekst;
        melding.temagruppeNavn = temagruppe;
        melding.journalfortTemanavn = arkivtema;
        melding.statusTekst = statustekst;
        melding.kanal = kanal;
        return melding;
    }

    private static List<Melding> alleMeldinger(List<Traad> traader) {
        return on(traader).flatmap(new Transformer<Traad, List<Melding>>() {
            @Override
            public List<Melding> transform(Traad traad) {
                return traad.meldinger;
            }
        }).collect();
    }

}