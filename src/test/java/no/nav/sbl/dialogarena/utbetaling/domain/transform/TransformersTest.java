package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.*;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer.AktoerType;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.Transformers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;

public class TransformersTest {

    @Test
    public void skattTransformererKorrektFraWSObjekt() {
        WSSkatt wsSkatt = new WSSkatt()
                .withSkattebeloep(-20.0);

        Double skattTransformed = SKATT_TRANSFORMER.transform(wsSkatt);
        assertThat(skattTransformed, is(-20.0));
    }

    @Test
    public void hovedytelsesDatoErUtbetalingsdatoNaarUtbetalingsdatoEksisterer() {
        WSUtbetaling wsUtbetaling = new WSUtbetaling()
                .withUtbetalingsdato(new DateTime(2010, 1, 1, 1, 1))
                .withPosteringsdato(new DateTime(2013, 2, 2, 2, 2));

        DateTime hovedytelseDato = determineHovedytelseDato(wsUtbetaling);
        assertThat(hovedytelseDato, is(new DateTime(2010, 1, 1, 1, 1)));
    }
    
    @Test
    public void hovedytelsesDatoErPosteringsdatoNaarUtbetalingsdatoIkkeEksisterer() {
        WSUtbetaling wsUtbetaling = new WSUtbetaling()
                .withPosteringsdato(new DateTime(2013, 2, 2, 2, 2));

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

        Record<Underytelse> underytelse = UNDERYTELSE_TRANSFORMER.transform(wsYtelseskomponent);
        assertThat(underytelse.get(Underytelse.ytelsesType), is("KompType"));
        assertThat(underytelse.get(Underytelse.ytelseBeloep), is(-10.0));
        assertThat(underytelse.get(Underytelse.satsAntall), is(2.0));
        assertThat(underytelse.get(Underytelse.satsBeloep), is(20.0));
        assertThat(underytelse.get(Underytelse.satsType), is("SatsType"));
    }

    @Test
    public void createAktoerFraWSObjekt() {
        WSPerson wsAktoer = new WSPerson().withAktoerId("***REMOVED***").withNavn("Ola Normann");
        Record<? extends Aktoer> aktoer = createAktoer(wsAktoer);
        assertThat(aktoer.get(Aktoer.aktoerId), is("***REMOVED***"));
        assertThat(aktoer.get(Aktoer.navn), is("Ola Normann"));
    }

    @Test
    public void createAktoerNaarAktoerErNull() {
        Record<? extends Aktoer> aktoer = createAktoer(null);
        assertNull(aktoer);
    }

    @Test
    public void createKontoFraWSObjekt() {
        WSBankkonto wsKonto = new WSBankkonto().withKontonummer("1122334455").withKontotype("Bankkonto - Innland");
        Record<Konto> konto = createKonto(wsKonto);
        assertThat(konto.get(Konto.kontonummer), is("1122334455"));
        assertThat(konto.get(Konto.kontotype), is("Bankkonto - Innland"));
    }

    @Test
    public void createKontoNaarKontoErNull() {
        Record<Konto> konto = createKonto(null);
        assertNull(konto);
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

        Optional<List<Record<Underytelse>>> underytelser = createUnderytelser(wsListe);

        assertThat(underytelser.get().size(), is(3));
        assertThat(underytelser.get().get(0).get(Underytelse.ytelsesType), is("SecondEntry"));
        assertThat(underytelser.get().get(1).get(Underytelse.ytelsesType), is("ThirdEntry"));
        assertThat(underytelser.get().get(2).get(Underytelse.ytelsesType), is("FirstEntry"));
    }

    @Test
    public void createUnderytelserNaarYtelseskomponenterErNull() {
        Optional<List<Record<Underytelse>>> underytelser = createUnderytelser(null);
        assertFalse(underytelser.isSome());
    }

    @Test
    public void createTrekklisteFraWSObjekter() {
        List<WSTrekk> wsTrekkListe = asList(
            new WSTrekk().withKreditor("First").withTrekkbeloep(-200),
            new WSTrekk().withKreditor("Second").withTrekkbeloep(100),
            new WSTrekk().withKreditor("Third").withTrekkbeloep(0)
        );

        List<Record<Trekk>> trekkliste = createTrekkliste(wsTrekkListe);
        assertThat(trekkliste.size(), is(3));
    }

    @Test
    public void createTrekkListeNaarTrekkErNull() {
        List<Record<Trekk>> trekkliste = createTrekkliste(null);
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

        Double totalSkatt = on(skatteListe).reduce(sumDouble);
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
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.nettoUtbetalt, 1000.0)
                .with(Hovedytelse.sumTrekk, -100.0)
                .with(Hovedytelse.sumSkatt, -100.0);

        Double bruttoBeloep = aggregateBruttoBeloep(hovedytelse);
        assertThat(bruttoBeloep, is(800.0));
    }

    @Test
    public void aggregateBruttoBeloepNaarSkattOgTrekkMangler() {
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.nettoUtbetalt, 1000.0);

        Double bruttoBeloep = aggregateBruttoBeloep(hovedytelse);
        assertThat(bruttoBeloep, is(1000.0));
    }

    @Test
    public void aggregateBruttoBeloepNaarAlleVerdierMangler() {
        Record<Hovedytelse> hovedytelse = new Record<>();

        Double bruttoBeloep = aggregateBruttoBeloep(hovedytelse);
        assertThat(bruttoBeloep, is(0.0));
    }

    @Test
    public void aggregateTrekkBeloepNaarAlleFelterFinnes() {
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.sumTrekk, -20.0)
                .with(Hovedytelse.sumSkatt, -10.0);

        Double trekkBeloep = aggregateTrekkBeloep(hovedytelse);
        assertThat(trekkBeloep, is(-30.0));
    }

    @Test
    public void aggregateTrekkBeloepNaarSkattMangler() {
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.sumTrekk, -20.0);

        Double trekkBeloep = aggregateTrekkBeloep(hovedytelse);
        assertThat(trekkBeloep, is(-20.0));
    }

    @Test
    public void aggregateTrekkBeloepNaarTrekkMangler() {
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.sumSkatt, -10.0);

        Double trekkBeloep = aggregateTrekkBeloep(hovedytelse);
        assertThat(trekkBeloep, is(-10.0));
    }

    @Test
    public void hovedytelseTransformererKorrektFraWSObjekt() {
        WSUtbetaling wsUtbetaling = new WSUtbetaling()
                .withUtbetaltTil(new WSPerson().withNavn("Ola Normann").withAktoerId("123123123").withDiskresjonskode("5"))
                .withPosteringsdato(new DateTime(2015, 1, 1, 13, 37))
                .withUtbetalingsmelding("Dette er en melding")
                .withUtbetalingsdato(new DateTime(2015, 1, 2, 3, 4))
                .withForfallsdato(new DateTime(2000, 1, 1, 1, 1))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Bankkonto").withKontonummer("***REMOVED***3"))
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
                                .withRettighetshaver(new WSPerson().withAktoerId("***REMOVED***6").withNavn("Kari Normann").withDiskresjonskode("3"))
                                .withRefundertForOrg(new WSOrganisasjon().withAktoerId("***REMOVED***").withNavn("KariNormann AS")));

        List<Record<Hovedytelse>> hovedytelser = TO_HOVEDYTELSE.transform(wsUtbetaling);
        assertThat(hovedytelser.size(), is(1));

        Record<Hovedytelse> ytelse = hovedytelser.get(0);
        assertThat(ytelse.get(Hovedytelse.mottakertype), is(Mottakertype.BRUKER));
        assertThat(ytelse.get(Hovedytelse.hovedytelsedato), is(new DateTime(2015, 1, 2, 3, 4)));
        assertThat(ytelse.get(Hovedytelse.utbetaltTil), is(new Record<Aktoer>().with(Aktoer.aktoerId, "123123123").with(Aktoer.navn, "Ola Normann").with(Aktoer.diskresjonskode, "5").with(Aktoer.aktoerType, AktoerType.PERSON)));
        assertThat(ytelse.get(Hovedytelse.utbetalingsmelding), is("Dette er en melding"));
        assertThat(ytelse.get(Hovedytelse.utbetaltTilKonto), is(new Record<Konto>().with(Konto.kontonummer, "***REMOVED***3").with(Konto.kontotype, "Bankkonto")));
        assertThat(ytelse.get(Hovedytelse.utbetalingsmetode), is("Overføring via bank"));
        assertThat(ytelse.get(Hovedytelse.utbetalingsstatus), is("Utbetalt"));
        assertThat(ytelse.get(Hovedytelse.forfallsdato), is(new DateTime(2000, 1, 1, 1, 1)));
        assertThat(ytelse.get(Hovedytelse.id), is(notNullValue()));
        assertThat(ytelse.get(Hovedytelse.ytelse), is("Dagpenger"));
        assertThat(ytelse.get(Hovedytelse.ytelsesperiode), is(new Interval(new DateTime(2000, 1, 1, 1, 2), new DateTime(2001, 1, 1, 1, 1))));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).size(), is(2));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(0).get(Underytelse.ytelsesType), is("Særtillegg"));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(0).get(Underytelse.satsAntall), is(12.0));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(0).get(Underytelse.ytelseBeloep), is(20000.0));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(0).get(Underytelse.satsType), is("SatsSats"));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(0).get(Underytelse.satsBeloep), is(20.0));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(1).get(Underytelse.ytelsesType), is("Grunnbeløp"));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(1).get(Underytelse.satsAntall), is(2.0));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(1).get(Underytelse.ytelseBeloep), is(200.0));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(1).get(Underytelse.satsType), is("SatsType"));
        assertThat(ytelse.get(Hovedytelse.underytelseListe).get(1).get(Underytelse.satsBeloep), is(10.0));
        assertThat(ytelse.get(Hovedytelse.trekkListe).size(), is(3));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(0).get(Trekk.kreditor), is("kreditor as"));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(0).get(Trekk.trekkBeloep), is(-2000.0));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(0).get(Trekk.trekksType), is("kreditortrekk"));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(1).get(Trekk.kreditor), is("kreditor ans"));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(1).get(Trekk.trekkBeloep), is(-3000.0));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(1).get(Trekk.trekksType), is("kreditortrekk"));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(2).get(Trekk.kreditor), is("kreditor enk"));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(2).get(Trekk.trekkBeloep), is(-4000.0));
        assertThat(ytelse.get(Hovedytelse.trekkListe).get(2).get(Trekk.trekksType), is("kreditortrekk"));
        assertThat(ytelse.get(Hovedytelse.sumTrekk), is(-9000.0));
        assertThat(ytelse.get(Hovedytelse.skattListe).size(), is(3));
        assertThat(ytelse.get(Hovedytelse.skattListe).get(0), is(-1.0));
        assertThat(ytelse.get(Hovedytelse.skattListe).get(1), is(-2.0));
        assertThat(ytelse.get(Hovedytelse.skattListe).get(2), is(-3.0));
        assertThat(ytelse.get(Hovedytelse.sumSkatt), is(-6.0));
        assertThat(ytelse.get(Hovedytelse.nettoUtbetalt), is(12994.0));
        assertThat(ytelse.get(Hovedytelse.bilagsnummer), is("123456789"));
        assertThat(ytelse.get(Hovedytelse.rettighetshaver), is(new Record<Aktoer>().with(Aktoer.navn, "Kari Normann").with(Aktoer.aktoerId, "***REMOVED***6").with(Aktoer.diskresjonskode, "3").with(Aktoer.aktoerType, AktoerType.PERSON)));
        assertThat(ytelse.get(Hovedytelse.refundertForOrg), is(new Record<Aktoer>().with(Aktoer.navn, "KariNormann AS").with(Aktoer.aktoerId, "***REMOVED***").with(Aktoer.aktoerType, AktoerType.ORGANISASJON)));

        assertThat(ytelse.get(Hovedytelse.sammenlagtTrekkBeloep), is(-9006.0));
        assertThat(ytelse.get(Hovedytelse.bruttoUtbetalt), is(22000.0));
    }
}