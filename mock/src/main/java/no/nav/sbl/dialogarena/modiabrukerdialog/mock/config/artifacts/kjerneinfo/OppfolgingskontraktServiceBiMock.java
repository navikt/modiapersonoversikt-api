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

    private static final String OPPFOELGINGSPUNKT_STATUS_FULLFORT = "Fullført";
    private static final String OPPFOELGINGSPUNKT_STATUS_AVBRUTT = "Avbrutt";
    private static final String OPPFOELGINGSPUNKTTYPE_SAMTALE = "Individuell samtale";
    private static final String OPPFOELGINGSPUNKTTYPE_GRUPPEAKTIVITETER = "Gruppeaktiviteter";
    private static final String OPPFOELGINGSPUNKT_AKTIVITETSNAVN_UTREDNING = "Utredning i spesialenhet/andrelinje";
    private static final String OPPFOELGINGSPUNKT_AKTIVITETSNAVN_INFO_AAP = "Informasjonsmøte om arbeidsavklaringspenger";
    private static final String AKTIVITETSTATUS_PLANLAGT = "Planlagt";
    private static final String AKTIVITETSTATUS_AVBRUTT = "Avbrutt";
    private static final String AKTIVITETSTATUS_FULLFORT = "Fullført";
    private static final String AKTIVITETSTATUS_AVBRUTT_GJONNOMFORING = "Avbrutt gjennomføring";
    private static final String PLAN_HOVEDMAAL_BEHOLDE_ARBEID = "Beholde arbeid";
    private static final String PLAN_HOVEDMAAL_SKAFFE_ARBEID = "Skaffe arbeid";
    private static final String PLAN_HOVEDMAAL_OKE_DELTAGELSE = "Øke deltakelse eller mål om arbeid";
    private static final String PLANSTATUS_GODKJENT = "Godkjent";
    private static final String PLANSTATUS_AVBRUTT = "Avbrutt";
    private static final String PLANSTATUS_ERSTATTET_AV_NY = "Erstattet av ny";
    private static final String PLANTYPE_AKTIVITET = "aktivitetsplan";
    private static final String PLANTYPE_INDIVIDUELL = "individuellPlan";
    private static final String PLANTYPE_KVLIFISERINGSPROGRAM = "Kvalifieringsprogram";
    private static final String TILTAKSDELTAKELSESTATUS_AKTUELL = "Aktuell";
    private static final String TILTAKSDELTAKELSESTATUS_FULLFORT = "Fullført";
    private static final String TILTAKSDELTAKELSESTATUS_GJENNOMFORES = "Gjennomføres";

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
        FimOppOppfoelgingskontrakt kontrakt = new FimOppOppfoelgingskontrakt();
        kontrakt.setGjelderBruker(createBruker());
        kontrakt.setStatus("Aktiv");

        kontrakt.setIhtGjeldendeVedtak(createVedtak(null, null, null, null));
        kontrakt.withAvYtelse(createYtelseskontrakt(null, null, DateUtils.getRandomDate()));

        kontrakt.withHarOppfoelgingspunkt(
                createOppfoelgingspunkt(OPPFOELGINGSPUNKTTYPE_SAMTALE, OPPFOELGINGSPUNKT_STATUS_AVBRUTT, DateUtils.getRandomLocalDateTime()),
                createOppfoelgingspunkt(OPPFOELGINGSPUNKTTYPE_SAMTALE, OPPFOELGINGSPUNKT_STATUS_FULLFORT, DateUtils.getRandomLocalDateTime()),
                createOppfoelgingspunkt(OPPFOELGINGSPUNKTTYPE_GRUPPEAKTIVITETER, OPPFOELGINGSPUNKT_AKTIVITETSNAVN_UTREDNING, DateUtils.getRandomLocalDateTime()),
                createOppfoelgingspunkt(OPPFOELGINGSPUNKTTYPE_GRUPPEAKTIVITETER, OPPFOELGINGSPUNKT_AKTIVITETSNAVN_INFO_AAP, DateUtils.getRandomLocalDateTime())
        );

        kontrakt.withMedAktivitet(
                createAktivitet("Aktivitetsnavn 1", AKTIVITETSTATUS_PLANLAGT, null, null, true),
                createAktivitet("Aktivitetsnavn 2", AKTIVITETSTATUS_AVBRUTT, null, null, false),
                createAktivitet("Aktivitetsnavn 3", AKTIVITETSTATUS_FULLFORT, null, null, false),
                createAktivitet("Aktivitetsnavn 4", AKTIVITETSTATUS_AVBRUTT_GJONNOMFORING, null, null, false),
                createTiltaksaktivitet(AKTIVITETSTATUS_PLANLAGT, TILTAKSDELTAKELSESTATUS_AKTUELL, null, null),
                createTiltaksaktivitet(AKTIVITETSTATUS_PLANLAGT, TILTAKSDELTAKELSESTATUS_FULLFORT, null, null),
                createTiltaksaktivitet(AKTIVITETSTATUS_AVBRUTT_GJONNOMFORING, TILTAKSDELTAKELSESTATUS_GJENNOMFORES, null, null)
        );

        kontrakt.withMedPlan(
                createPlan(PLANSTATUS_GODKJENT, PLANTYPE_AKTIVITET, PLAN_HOVEDMAAL_BEHOLDE_ARBEID, null, null),
                createPlan(PLANSTATUS_AVBRUTT, PLANTYPE_INDIVIDUELL, PLAN_HOVEDMAAL_OKE_DELTAGELSE, null, null),
                createPlan(PLANSTATUS_ERSTATTET_AV_NY, PLANTYPE_KVLIFISERINGSPROGRAM, PLAN_HOVEDMAAL_SKAFFE_ARBEID, null, null)
        );

        return kontrakt;
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
