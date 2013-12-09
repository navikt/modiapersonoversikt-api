package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.common.mockutils.DateUtils;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockFactory;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
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

import java.util.Date;

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
        return new OppfolgingskontraktMapper().map(respons, OppfolgingskontraktResponse.class);
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
        FimOppPlan plan = new FimOppPlan();
        plan.setHovedmaal(hovedmal);
        if (fra == null) {
            plan.setPeriode(createRandomPeriode());
        } else {
            plan.setPeriode(createPeriode(fra, til));
        }
        plan.setPlanstatus(status);
        plan.setPlantype(type);
        return plan;
    }

    private static FimOppPeriode createPeriode(Date fom, Date tom) {
        FimOppPeriode periode = new FimOppPeriode();
        periode.setFom(DateUtils.convertDateToXmlGregorianCalendar(fom));
        periode.setTom(DateUtils.convertDateToXmlGregorianCalendar(tom));
        return periode;
    }

    private static FimOppPeriode createRandomPeriode() {
        Date[] datePair = DateUtils.getRandomDatePair();
        return createPeriode(datePair[0], datePair[1]);
    }


    private static FimOppTiltaksaktivitet createTiltaksaktivitet(String status, String deltakelsestatus, Date fra, Date til) {
        FimOppTiltaksaktivitet aktivitet = new FimOppTiltaksaktivitet();
        aktivitet.setStatus(status);
        if (fra == null) {
            aktivitet.setPeriode(createRandomPeriode());
        } else {
            aktivitet.setPeriode(createPeriode(fra, til));
        }
        aktivitet.setAktivitetsnavn("Tiltaksaktivitetnavn");
        aktivitet.setTiltaksdeltakelsestatus(deltakelsestatus);
        return aktivitet;
    }

    private static FimOppAktivitet createAktivitet(String navn, String status, Date fra, Date til, boolean sensitiv) {
        FimOppAktivitet aktivitet = new FimOppAktivitet();
        aktivitet.setAktivitetsnavn(navn);
        if (fra == null) {
            aktivitet.setPeriode(createRandomPeriode());
        } else {
            aktivitet.setPeriode(createPeriode(fra, til));
        }
        aktivitet.setStatus(status);
        aktivitet.setSensitiv(sensitiv);
        return aktivitet;
    }

    private static FimOppOppfoelgingspunkt createOppfoelgingspunkt(String type, String status, LocalDateTime trefftidspunkt) {
        FimOppOppfoelgingspunkt punkt = new FimOppOppfoelgingspunkt();
        punkt.setDato(DateUtils.convertDateTimeToXmlGregorianCalendar(trefftidspunkt));
        punkt.setStatus(status);
        punkt.setType(type);
        return punkt;
    }

    private static FimOppYtelseskontrakt createYtelseskontrakt(String status, String type, Date datoKravMottat) {
        FimOppYtelseskontrakt kontrakt = new FimOppYtelseskontrakt();

        if (datoKravMottat == null) {
            kontrakt.setDatoKravMottatt(DateUtils.convertDateToXmlGregorianCalendar(new Date()));
        } else {
            kontrakt.setDatoKravMottatt(DateUtils.convertDateToXmlGregorianCalendar(datoKravMottat));
        }

        if (status == null) {
            kontrakt.setStatus("YtelseskontraktStatus");
        } else {
            kontrakt.setStatus(status);
        }

        if (type == null) {
            kontrakt.setYtelsestype("Ytelsestype");
        } else {
            kontrakt.setYtelsestype(type);
        }
        return kontrakt;
    }

    private static FimOppVedtak createVedtak(String status, Date fra, Date til, Date datoKravMottatt) {
        FimOppVedtak vedtak = new FimOppVedtak();
        if (status == null) {
            vedtak.setStatus(YtelseskontraktMockFactory.YTELSESSTATUS_AKTIV);
        } else {
            vedtak.setStatus(status);
        }

        if (fra == null) {
            vedtak.setVedtaksperiode(createPeriode(new Date(), new Date()));
        } else {
            vedtak.setVedtaksperiode(createPeriode(fra, til));
        }

        if (datoKravMottatt == null) {
            vedtak.setOmYtelse(createYtelseskontrakt(null, null, new Date()));
        } else {
            vedtak.setOmYtelse(createYtelseskontrakt(null, null, datoKravMottatt));
        }
        return vedtak;
    }

    private static FimOppBruker createBruker() {
        FimOppBruker bruker = new FimOppBruker();
        bruker.setFormidlingsgruppe("50000");
        bruker.withServicegruppe(createServicegruppe());
        bruker.withMeldeplikt(createMeldeplikt());
        return bruker;
    }

    private static FimOppMeldeplikt createMeldeplikt() {
        FimOppMeldeplikt meldeplikt = new FimOppMeldeplikt();
        meldeplikt.setMeldeplikt(true);
        return meldeplikt;
    }

    private static FimOppServiceGruppe createServicegruppe() {
        FimOppServiceGruppe serviceGruppe = new FimOppServiceGruppe();
        serviceGruppe.setServiceGruppe("Servicegruppe");
        return serviceGruppe;
    }

}
