package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.*;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer.AktoerType;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.Transformers.*;
import static org.hamcrest.Matchers.*;
import static org.joda.time.DateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class TransformersTest {

    public static final String AKTOER_ID = "1";
    public static final String KONTONUMMER = "123";
    public static final String ORGANISASJON_AKTOER_ID = "5";

    @Test
    public void skattTransformererKorrektFraWSObjekt() {
        WSSkatt wsSkatt = new WSSkatt()
                .withSkattebeloep(-20.0);

        Double skattTransformed = SKATT_TRANSFORMER.apply(wsSkatt);
        assertThat(skattTransformed, is(-20.0));
    }

    @Test
    public void hovedytelsesDatoErUtbetalingsdatoNaarUtbetalingsdatoEksisterer() {
        WSUtbetaling wsUtbetaling = new WSUtbetaling()
                .withUtbetalingsdato(new DateTime(2010, 1, 1, 1, 1))
                .withForfallsdato(new DateTime(2011, 1, 1, 1, 1))
                .withPosteringsdato(new DateTime(2013, 2, 2, 2, 2));

        DateTime hovedytelseDato = determineHovedytelseDato(wsUtbetaling);
        assertThat(hovedytelseDato, is(new DateTime(2010, 1, 1, 1, 1)));
    }

    @Test
    public void hovedytelsesDatoErPosteringsdatoNaarUtbetalingsdatoOgForfallsdatoIkkeEksisterer() {
        WSUtbetaling wsUtbetaling = new WSUtbetaling()
                .withPosteringsdato(new DateTime(2013, 2, 2, 2, 2));

        DateTime hovedytelseDato = determineHovedytelseDato(wsUtbetaling);
        assertThat(hovedytelseDato, is(new DateTime(2013, 2, 2, 2, 2)));
    }

    @Test
    public void hovedytelsesDatoErForfallsdatoNaarUtbetalingsdatoIkkeEksisterer() {
        WSUtbetaling wsUtbetaling = new WSUtbetaling()
                .withForfallsdato(new DateTime(2013, 2, 2, 2, 2));

        DateTime hovedytelseDato = determineHovedytelseDato(wsUtbetaling);
        assertThat(hovedytelseDato, is(new DateTime(2013, 2, 2, 2, 2)));
    }

    @Test
    public void underytelseTransformererKorrektFraWSObjekt() {
        WSYtelseskomponent wsYtelseskomponent = new WSYtelseskomponent()
                .withYtelseskomponenttype("KompType")
                .withYtelseskomponentbeloep(-10.0)
                .withSatsantall(2.0)
                .withSatsbeloep(20.0)
                .withSatstype("SatsType");

        Underytelse underytelse = UNDERYTELSE_TRANSFORMER.apply(wsYtelseskomponent);
        assertThat(underytelse.getYtelsesType(), is("KompType"));
        assertThat(underytelse.getYtelseBeloep(), is(-10.0));
        assertThat(underytelse.getSatsAntall(), is(2.0));
        assertThat(underytelse.getSatsBeloep(), is(20.0));
        assertThat(underytelse.getSatsType(), is("SatsType"));
    }

    @Test
    public void underytelseTransformererUtenSatsType() {
        WSYtelseskomponent wsYtelseskomponent = new WSYtelseskomponent()
                .withYtelseskomponenttype("KompType")
                .withYtelseskomponentbeloep(-10.0)
                .withSatsantall(2.0)
                .withSatsbeloep(20.0);

        Underytelse underytelse = UNDERYTELSE_TRANSFORMER.apply(wsYtelseskomponent);
        assertThat(underytelse.getYtelsesType(), is("KompType"));
        assertThat(underytelse.getYtelseBeloep(), is(-10.0));
        assertThat(underytelse.getSatsAntall(), is(2.0));
        assertThat(underytelse.getSatsBeloep(), is(20.0));
    }

    @Test
    public void createAktoerFraWSObjekt() {
        WSPerson wsAktoer = new WSPerson().withAktoerId(AKTOER_ID).withNavn("Ola Normann");
        Aktoer aktoer = createAktoer(wsAktoer);
        assertThat(aktoer.getAktoerId(), is(AKTOER_ID));
        assertThat(aktoer.getNavn(), is("Ola Normann"));
    }

    @Test
    public void createAktoerNaarAktoerErNull() {
        Aktoer aktoer = createAktoer(null);
        assertNull(aktoer);
    }

    @Test
    public void kontoErKontonummerFraRespons() {
        WSUtbetaling utbetaling = new WSUtbetaling().withUtbetaltTilKonto(new WSBankkonto().withKontonummer(KONTONUMMER).withKontotype("Bankkonto - Innland")).withUtbetalingsmetode("Norsk bankkonto");
        String konto = determineKontoUtbetaltTil(utbetaling);
        assertThat(konto, is(KONTONUMMER));
    }

    @Test
    public void kontoErUtbetalingsmetodeHvisKontoIkkeFinnesIRespons() {
        String konto = determineKontoUtbetaltTil(new WSUtbetaling().withUtbetalingsmetode("Norsk utbetalingskort"));
        assertThat(konto, is("Norsk utbetalingskort"));
    }

    @Test
    public void kontoErUtbetalingsmetodeHvisKontoErTomIRespons() {
        String konto = determineKontoUtbetaltTil(new WSUtbetaling().withUtbetaltTilKonto(new WSBankkonto()).withUtbetalingsmetode("Norsk utbetalingskort"));
        assertThat(konto, is("Norsk utbetalingskort"));
    }

    @Test
    public void createPeriodeFraWSObjekt() {
        DateTime fom = now();
        DateTime tom = now().plusDays(1);
        WSPeriode wsPeriode = new WSPeriode().withFom(fom).withTom(tom);

        Interval periode = createPeriode(wsPeriode);
        assertThat(periode.getStart(), is(fom));
        assertThat(periode.getEnd(), is(tom));
    }

    @Test
    public void createPeriodeNaarPeriodeErNull() {
        Interval periode = createPeriode(null);
        assertNotNull(periode);
        assertThat(periode.getStart().getMillisOfSecond(), is(0));
        assertThat(periode.getEnd().getMillisOfSecond(), is(0));
    }

    @Test
    public void createSorterteUnderytelserPaaBeloepFraWSObjekter() {
        List<WSYtelseskomponent> wsListe = asList(
                new WSYtelseskomponent()
                        .withYtelseskomponentbeloep(-200.0)
                        .withYtelseskomponenttype("FirstEntry"),
                new WSYtelseskomponent()
                        .withYtelseskomponentbeloep(100.0)
                        .withYtelseskomponenttype("SecondEntry"),
                new WSYtelseskomponent()
                        .withYtelseskomponentbeloep(0.0)
                        .withYtelseskomponenttype("ThirdEntry")
                );

        List<Underytelse> underytelser = createUnderytelser(wsListe);

        assertThat(underytelser.size(), is(3));
        assertThat(underytelser.get(0).getYtelsesType(), is("SecondEntry"));
        assertThat(underytelser.get(1).getYtelsesType(), is("ThirdEntry"));
        assertThat(underytelser.get(2).getYtelsesType(), is("FirstEntry"));
    }

    @Test
    public void createUnderytelserNaarYtelseskomponenterErNull() {
        List<Underytelse> underytelser = createUnderytelser(null);
        assertFalse(underytelser == null);
    }

    @Test
    public void createTrekklisteFraWSObjekter() {
        List<WSTrekk> wsTrekkListe = asList(
            new WSTrekk().withKreditor("First").withTrekkbeloep(-200),
            new WSTrekk().withKreditor("Second").withTrekkbeloep(100),
            new WSTrekk().withKreditor("Third").withTrekkbeloep(0)
        );

        List<Trekk> trekkliste = createTrekkliste(wsTrekkListe);
        assertThat(trekkliste.size(), is(3));
    }

    @Test
    public void createTrekkListeNaarTrekkErNull() {
        List<Trekk> trekkliste = createTrekkliste(null);
        assertNotNull(trekkliste);
        assertTrue(trekkliste.isEmpty());
    }

    @Test
    public void createSkatteListeFraWSObjekter() {
        List<WSSkatt> wsSkatteListe = asList(
            new WSSkatt().withSkattebeloep(-100.0),
            new WSSkatt().withSkattebeloep(-200.0),
            new WSSkatt().withSkattebeloep(-300.0),
            new WSSkatt().withSkattebeloep(-400.0),
            new WSSkatt().withSkattebeloep(-500.0)
        );

        List<Double> skatteListe = createSkatteListe(wsSkatteListe);
        assertThat(skatteListe.size(), is(5));

        Double totalSkatt = skatteListe.stream().reduce((aDouble, aDouble2) -> aDouble+aDouble2).get();
        assertThat(totalSkatt, is(-1500.0));
    }

    @Test
    public void createSkattelisteNaarSkattelisteErNull() {
        List<Double> skatteListe = createSkatteListe(null);
        assertNotNull(skatteListe);
        assertTrue(skatteListe.isEmpty());
    }

    @Test
    public void aggregereBruttoBeloepNaarAlleFelterFinnes() {
        Hovedytelse hovedytelse = new Hovedytelse()
                .withNettoUtbetalt(1000.0)
                .withSumTrekk(-100.0)
                .withSumSkatt(-100.0);

        Double bruttoBeloep = aggregateBruttoBeloep(hovedytelse);
        assertThat(bruttoBeloep, is(800.0));
    }

    @Test
    public void aggregateBruttoBeloepNaarSkattOgTrekkMangler() {
        Hovedytelse hovedytelse = new Hovedytelse()
                .withNettoUtbetalt(1000.0);

        Double bruttoBeloep = aggregateBruttoBeloep(hovedytelse);
        assertThat(bruttoBeloep, is(1000.0));
    }

    @Test
    public void aggregateBruttoBeloepNaarAlleVerdierMangler() {
        Hovedytelse hovedytelse = new Hovedytelse();

        Double bruttoBeloep = aggregateBruttoBeloep(hovedytelse);
        assertThat(bruttoBeloep, is(0.0));
    }

    @Test
    public void aggregateTrekkBeloepNaarAlleFelterFinnes() {
        Hovedytelse hovedytelse = new Hovedytelse()
                .withSumTrekk(-20.0)
                .withSumSkatt(-10.0)
                .withSammenlagtTrekkBeloep();

        Double trekkBeloep = hovedytelse.getSammenlagtTrekkBeloep();
        assertThat(trekkBeloep, is(-30.0));
    }

    @Test
    public void aggregateTrekkBeloepNaarSkattMangler() {
        Hovedytelse hovedytelse = new Hovedytelse()
                .withSumTrekk(-20.0)
                .withSammenlagtTrekkBeloep();

        Double trekkBeloep = hovedytelse.getSammenlagtTrekkBeloep();
        assertThat(trekkBeloep, is(-20.0));
    }

    @Test
    public void aggregateTrekkBeloepNaarTrekkMangler() {
        Hovedytelse hovedytelse = new Hovedytelse()
                .withSumSkatt(-10.0)
                .withSammenlagtTrekkBeloep();

        Double trekkBeloep = hovedytelse.getSammenlagtTrekkBeloep();
        assertThat(trekkBeloep, is(-10.0));
    }

    @Test
    public void transformererIntervalKorrekt() {
        DateTime start = new DateTime(2015, 1, 1, 1, 1);
        DateTime end = new DateTime(2015, 1, 2, 1, 1);
        Interval periode = Transformers.createPeriode(new WSPeriode().withFom(start).withTom(end));
        assertNotNull(periode);
        assertThat(periode.getStart(), is(start));
        assertThat(periode.getEnd(), is(end));
    }

    @Test
    public void tomPeriodeBlirINtervallFraUnixEpoch() {
        Interval periode = Transformers.createPeriode(new WSPeriode());
        assertNotNull(periode);
        assertThat(periode.getStart(), is(new DateTime(1970, 1, 1, 1, 0)));
        assertThat(periode.getEnd(), is(new DateTime(1970, 1, 1, 1, 0)));
    }

    @Test
    public void nullPeriodeBlirIntervallFraUnixEpoch() {
        Interval periode = Transformers.createPeriode(null);
        assertNotNull(periode);
        assertThat(periode.getStart(), is(new DateTime(1970, 1, 1, 1, 0)));
        assertThat(periode.getEnd(), is(new DateTime(1970, 1, 1, 1, 0)));
    }

    @Test
    public void hovedytelseTransformererKorrektFraWSObjekt() {
        WSUtbetaling wsUtbetaling = new WSUtbetaling()
                .withUtbetaltTil(new WSPerson().withNavn("Ola Normann").withAktoerId(AKTOER_ID).withDiskresjonskode("5"))
                .withPosteringsdato(new DateTime(2015, 1, 1, 13, 37))
                .withUtbetalingsmelding("Dette er en melding")
                .withUtbetalingsdato(new DateTime(2015, 1, 2, 3, 4))
                .withForfallsdato(new DateTime(2000, 1, 1, 1, 1))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Bankkonto").withKontonummer(KONTONUMMER))
                .withUtbetalingsmetode("Overføring via bank")
                .withUtbetalingsstatus("Utbetalt")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Dagpenger"))
                                .withYtelsesperiode(new WSPeriode().withFom(new DateTime(2000, 1, 1, 1, 2)).withTom(new DateTime(2001, 1, 1, 1, 1)))
                                .withYtelseskomponentListe(
                                        new WSYtelseskomponent()
                                                .withYtelseskomponentbeloep(200.0)
                                                .withYtelseskomponenttype("Grunnbeløp")
                                                .withSatsantall(2.0)
                                                .withSatstype("SatsType")
                                                .withSatsbeloep(10.0),
                                        new WSYtelseskomponent()
                                                .withYtelseskomponentbeloep(20000.0)
                                                .withYtelseskomponenttype("Særtillegg")
                                                .withSatsantall(12.0)
                                                .withSatstype("SatsSats")
                                                .withSatsbeloep(20.0))
                                .withYtelseskomponentersum(22000.0)
                                .withTrekkListe(
                                        new WSTrekk().withKreditor("kreditor as").withTrekkbeloep(-2000.0).withTrekktype("kreditortrekk"),
                                        new WSTrekk().withKreditor("kreditor ans").withTrekkbeloep(-3000.0).withTrekktype("kreditortrekk"),
                                        new WSTrekk().withKreditor("kreditor enk").withTrekkbeloep(-4000.0).withTrekktype("kreditortrekk"))
                                .withTrekksum(-9000.0)
                                .withSkattListe(
                                        new WSSkatt().withSkattebeloep(-1.0),
                                        new WSSkatt().withSkattebeloep(-2.0),
                                        new WSSkatt().withSkattebeloep(-3.0))
                                .withSkattsum(-6.0)
                                .withYtelseNettobeloep(12994.0)
                                .withBilagsnummer("123456789")
                                .withRettighetshaver(new WSPerson().withAktoerId(AKTOER_ID).withNavn("Kari Normann").withDiskresjonskode("3"))
                                .withRefundertForOrg(new WSOrganisasjon().withAktoerId(ORGANISASJON_AKTOER_ID).withNavn("KariNormann AS")));

        List<Hovedytelse> hovedytelser = TO_HOVEDYTELSE.apply(wsUtbetaling);
        assertThat(hovedytelser.size(), is(1));

        Hovedytelse ytelse = hovedytelser.get(0);
        assertThat(ytelse.getMottakertype(), is(Mottakertype.BRUKER));
        assertThat(ytelse.getHovedytelsedato(), is(new DateTime(2015, 1, 2, 3, 4)));

        assertThat(ytelse.getUtbetaltTil(), instanceOf(Aktoer.class));
        assertThat(ytelse.getUtbetaltTil().getAktoerId(), is(AKTOER_ID));
        assertThat(ytelse.getUtbetaltTil().getNavn(), is("Ola Normann"));
        assertThat(ytelse.getUtbetaltTil().getDiskresjonskode(), is("5"));
        assertThat(ytelse.getUtbetaltTil().getAktoerType(), is(AktoerType.PERSON));

        assertThat(ytelse.getUtbetalingsmelding(), is("Dette er en melding"));
        assertThat(ytelse.getUtbetaltTilKonto(), is(KONTONUMMER));
        assertThat(ytelse.getUtbetalingsmetode(), is("Overføring via bank"));
        assertThat(ytelse.getUtbetalingsstatus(), is("Utbetalt"));
        assertThat(ytelse.getForfallsdato(), is(new DateTime(2000, 1, 1, 1, 1)));
        assertThat(ytelse.getId(), is(notNullValue()));
        assertThat(ytelse.getYtelse(), is("dagpenger"));
        assertThat(ytelse.getYtelsesperiode(), is(new Interval(new DateTime(2000, 1, 1, 1, 2), new DateTime(2001, 1, 1, 1, 1))));
        assertThat(ytelse.getUnderytelseListe().size(), is(2));
        assertThat(ytelse.getUnderytelseListe().get(0).getYtelsesType(), is("Særtillegg"));
        assertThat(ytelse.getUnderytelseListe().get(0).getSatsAntall(), is(12.0));
        assertThat(ytelse.getUnderytelseListe().get(0).getYtelseBeloep(), is(20000.0));
        assertThat(ytelse.getUnderytelseListe().get(0).getSatsType(), is("SatsSats"));
        assertThat(ytelse.getUnderytelseListe().get(0).getSatsBeloep(), is(20.0));
        assertThat(ytelse.getUnderytelseListe().get(1).getYtelsesType(), is("Grunnbeløp"));
        assertThat(ytelse.getUnderytelseListe().get(1).getSatsAntall(), is(2.0));
        assertThat(ytelse.getUnderytelseListe().get(1).getYtelseBeloep(), is(200.0));
        assertThat(ytelse.getUnderytelseListe().get(1).getSatsType(), is("SatsType"));
        assertThat(ytelse.getUnderytelseListe().get(1).getSatsBeloep(), is(10.0));
        assertThat(ytelse.getTrekkListe().size(), is(3));
        assertThat(ytelse.getTrekkListe().get(0).getKreditor(), is("kreditor as"));
        assertThat(ytelse.getTrekkListe().get(0).getTrekkBeloep(), is(-2000.0));
        assertThat(ytelse.getTrekkListe().get(0).getTrekksType(), is("kreditortrekk"));
        assertThat(ytelse.getTrekkListe().get(1).getKreditor(), is("kreditor ans"));
        assertThat(ytelse.getTrekkListe().get(1).getTrekkBeloep(), is(-3000.0));
        assertThat(ytelse.getTrekkListe().get(1).getTrekksType(), is("kreditortrekk"));
        assertThat(ytelse.getTrekkListe().get(2).getKreditor(), is("kreditor enk"));
        assertThat(ytelse.getTrekkListe().get(2).getTrekkBeloep(), is(-4000.0));
        assertThat(ytelse.getTrekkListe().get(2).getTrekksType(), is("kreditortrekk"));
        assertThat(ytelse.getSumTrekk(), is(-9000.0));
        assertThat(ytelse.getSkattListe().size(), is(3));
        assertThat(ytelse.getSkattListe().get(0), is(-1.0));
        assertThat(ytelse.getSkattListe().get(1), is(-2.0));
        assertThat(ytelse.getSkattListe().get(2), is(-3.0));
        assertThat(ytelse.getSumSkatt(), is(-6.0));
        assertThat(ytelse.getNettoUtbetalt(), is(12994.0));
        assertThat(ytelse.getBilagsnummer(), is("123456789"));

        assertThat(ytelse.getRettighetshaver(), instanceOf(Aktoer.class));
        assertThat(ytelse.getRettighetshaver().getNavn(), is("Kari Normann"));
        assertThat(ytelse.getRettighetshaver().getAktoerId(), is(AKTOER_ID));
        assertThat(ytelse.getRettighetshaver().getDiskresjonskode(), is("3"));
        assertThat(ytelse.getRettighetshaver().getAktoerType(), is(AktoerType.PERSON));

        assertThat(ytelse.getRefundertForOrg(), instanceOf(Aktoer.class));
        assertThat(ytelse.getRefundertForOrg().getNavn(), is("KariNormann AS"));
        assertThat(ytelse.getRefundertForOrg().getAktoerId(), is(ORGANISASJON_AKTOER_ID));
        assertThat(ytelse.getRefundertForOrg().getAktoerType(), is(AktoerType.ORGANISASJON));

        assertThat(ytelse.getSammenlagtTrekkBeloep(), is(-9006.0));
        assertThat(ytelse.getBruttoUtbetalt(), is(22000.0));
    }
}
