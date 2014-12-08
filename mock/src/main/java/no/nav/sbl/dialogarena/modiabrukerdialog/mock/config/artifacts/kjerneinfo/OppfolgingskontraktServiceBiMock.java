package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.common.mockutils.DateUtils;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.kontrakter.domain.oppfolging.SYFOPunkt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppAktivitet;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppBruker;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppMeldeplikt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppOppfoelgingskontrakt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppOppfoelgingspunkt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppPeriode;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppPlan;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppServiceGruppe;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppTiltaksaktivitet;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppVedtak;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.FimOppYtelseskontrakt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.FimOppHentOppfoelgingskontraktListeResponse;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static no.nav.kjerneinfo.common.mockutils.DateUtils.convertDateTimeToXmlGregorianCalendar;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.convertDateToXmlGregorianCalendar;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getRandomDatePair;
import static no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockFactory.YTELSESSTATUS_AKTIV;
import static org.joda.time.LocalDate.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OppfolgingskontraktServiceBiMock {

    public static OppfolgingskontraktServiceBi getOppfolgingskontraktServiceBiMock() {
        OppfolgingskontraktServiceBi mock = mock(OppfolgingskontraktServiceBi.class);
        when(mock.hentOppfolgingskontrakter(any(OppfolgingskontraktRequest.class))).thenReturn(lagOppfolgingsMockRespons());
        return mock;
    }

    private static OppfolgingskontraktResponse lagOppfolgingsMockRespons() {
        FimOppHentOppfoelgingskontraktListeResponse respons = new FimOppHentOppfoelgingskontraktListeResponse();
        respons.withOppfoelgingskontraktListe(createOppfoelgingskontrakt());
        OppfolgingskontraktResponse returRespons = new OppfolgingskontraktMapper().map(respons, OppfolgingskontraktResponse.class);
        returRespons.setSyfoPunkter(createSYFOpunkter());
        return returRespons;
    }

    private static List<SYFOPunkt> createSYFOpunkter() {
        List<SYFOPunkt> syfoPunkter = new ArrayList<>();
        SYFOPunkt syfoPunkt = new SYFOPunkt();
        syfoPunkt.setSyfoHendelse("Mottatt rapport");
        syfoPunkt.setStatus("Godkjent");
        syfoPunkt.setDato(now().minusMonths(1));
        syfoPunkter.add(syfoPunkt);
        syfoPunkter.add(syfoPunkt);
        return syfoPunkter;
    }

    private static FimOppOppfoelgingskontrakt createOppfoelgingskontrakt() {
        return new FimOppOppfoelgingskontrakt()
                .withGjelderBruker(createBruker())
                .withStatus("Aktiv")
                .withIhtGjeldendeVedtak(createVedtak(null, null, null, null))
                .withAvYtelse(createYtelseskontrakt(null, null, DateUtils.getRandomDate()))
                .withHarOppfoelgingspunkt(
                        createOppfoelgingspunkt("Individuell samtale", "Avbrutt", DateUtils.getRandomLocalDateTime()),
                        createOppfoelgingspunkt("Individuell samtale", "Fullført", DateUtils.getRandomLocalDateTime()),
                        createOppfoelgingspunkt("Gruppeaktiviteter", "Utredning i spesialenhet/andrelinje", DateUtils.getRandomLocalDateTime()),
                        createOppfoelgingspunkt("Gruppeaktiviteter", "Informasjonsmøte om arbeidsavklaringspenger", DateUtils.getRandomLocalDateTime()))
                .withMedAktivitet(
                        createAktivitet("Aktivitetsnavn 1", "Planlagt", null, null, true),
                        createAktivitet("Aktivitetsnavn 2", "Avbrutt", null, null, false),
                        createAktivitet("Aktivitetsnavn 3", "Fullført", null, null, false),
                        createAktivitet("Aktivitetsnavn 4", "Avbrutt gjennomføring", null, null, false),
                        createTiltaksaktivitet("Planlagt", "Aktuell", null, null),
                        createTiltaksaktivitet("Planlagt", "Fullført", null, null),
                        createTiltaksaktivitet("Avbrutt gjennomføring", "Gjennomføres", null, null))
                .withMedPlan(
                        createPlan("Godkjent", "aktivitetsplan", "Beholde arbeid", null, null),
                        createPlan("Avbrutt", "individuellPlan", "Øke deltakelse eller mål om arbeid", null, null),
                        createPlan("Erstattet av ny", "Kvalifieringsprogram", "Skaffe arbeid", null, null));
    }

    private static FimOppPlan createPlan(String status, String type, String hovedmal, Date fra, Date til) {
        return new FimOppPlan()
                .withHovedmaal(hovedmal)
                .withPlantype(type)
                .withPlanstatus(status)
                .withPeriode(fra == null ? createRandomPeriode() : createPeriode(fra, til));
    }

    private static FimOppPeriode createPeriode(Date fom, Date tom) {
        return new FimOppPeriode()
                .withFom(convertDateToXmlGregorianCalendar(fom))
                .withTom(convertDateToXmlGregorianCalendar(tom));
    }

    private static FimOppPeriode createRandomPeriode() {
        Date[] datePair = getRandomDatePair();
        return createPeriode(datePair[0], datePair[1]);
    }


    private static FimOppTiltaksaktivitet createTiltaksaktivitet(String status, String deltakelsestatus, Date fra, Date til) {
        return new FimOppTiltaksaktivitet()
                .withStatus(status)
                .withAktivitetsnavn("Tiltaksaktivitetnavn")
                .withTiltaksdeltakelsestatus(deltakelsestatus)
                .withPeriode(fra == null ? createRandomPeriode() : createPeriode(fra, til));
    }

    private static FimOppAktivitet createAktivitet(String navn, String status, Date fra, Date til, boolean sensitiv) {
        return new FimOppAktivitet()
                .withAktivitetsnavn(navn)
                .withStatus(status)
                .withSensitiv(sensitiv)
                .withPeriode(fra == null ? createRandomPeriode() : createPeriode(fra, til));
    }

    private static FimOppOppfoelgingspunkt createOppfoelgingspunkt(String type, String status, LocalDateTime trefftidspunkt) {
        return new FimOppOppfoelgingspunkt()
                .withDato(convertDateTimeToXmlGregorianCalendar(trefftidspunkt))
                .withStatus(status)
                .withType(type);
    }

    private static FimOppYtelseskontrakt createYtelseskontrakt(String status, String type, Date datoKravMottat) {
        return new FimOppYtelseskontrakt()
                .withDatoKravMottatt(datoKravMottat == null ? convertDateToXmlGregorianCalendar(new Date()) : convertDateToXmlGregorianCalendar(datoKravMottat))
                .withStatus(status == null ? "YtelseskontraktStatus" : status)
                .withYtelsestype(type == null ? "Ytelsestype" : type);
    }

    private static FimOppVedtak createVedtak(String status, Date fra, Date til, Date datoKravMottatt) {
        return new FimOppVedtak()
                .withStatus(status == null ? YTELSESSTATUS_AKTIV : status)
                .withVedtaksperiode(fra == null ? createRandomPeriode() : createPeriode(fra, til))
                .withOmYtelse(datoKravMottatt == null ? createYtelseskontrakt(null, null, new Date()) : createYtelseskontrakt(null, null, datoKravMottatt));
    }

    private static FimOppBruker createBruker() {
        return new FimOppBruker()
                .withFormidlingsgruppe("50000")
                .withServicegruppe(createServicegruppe())
                .withMeldeplikt(createMeldeplikt());
    }

    private static FimOppMeldeplikt createMeldeplikt() {
        return new FimOppMeldeplikt().withMeldeplikt(true);
    }

    private static FimOppServiceGruppe createServicegruppe() {
        return new FimOppServiceGruppe().withServiceGruppe("Servicegruppe");
    }

}
