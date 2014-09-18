package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Bruker;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.EndringsInfo;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Fagomradekode;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sakstypekode;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class ArbeidOgAktivitetMock {

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        return createArbeidOgAktivitetMock();
    }

    public static ArbeidOgAktivitet createArbeidOgAktivitetMock() {
        return new ArbeidOgAktivitet() {
            @Override
            public WSHentSakListeResponse hentSakListe(WSHentSakListeRequest request) {
                return new WSHentSakListeResponse().withSakListe(new ArrayList<>(Arrays.asList(
                        new Sak()
                                .withSaksId("12121212")
                                .withAnsvarligEnhetId("ansvarlig enhet 1")
                                .withAr("2013")
                                .withBruker(new Bruker().withBruker("bruker 1").withBrukertypeKode("type 1"))
                                .withEndringsInfo(new EndringsInfo().withOpprettetDato(new LocalDate().minusDays(2)))
                                .withFagomradeKode(new Fagomradekode().withKode("OPP"))
                                .withSakstypeKode(new Sakstypekode().withKode("ARBEID"))
                                .withLopenr("løpenr 1"),
                        new Sak()
                                .withSaksId("13131313")
                                .withAnsvarligEnhetId("ansvarlig enhet 2")
                                .withAr("2014")
                                .withBruker(new Bruker().withBruker("bruker 2").withBrukertypeKode("type 2"))
                                .withEndringsInfo(new EndringsInfo().withOpprettetDato(new LocalDate().minusDays(1)))
                                .withFagomradeKode(new Fagomradekode().withKode("ikke en oppfølgingssak!"))
                                .withSakstypeKode(new Sakstypekode().withKode("NOE ANNET"))
                                .withLopenr("løpenr 2"))));
            }
        };
    }

}
