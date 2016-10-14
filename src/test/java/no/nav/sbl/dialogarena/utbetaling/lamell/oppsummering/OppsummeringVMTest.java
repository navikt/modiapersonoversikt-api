package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;


public class OppsummeringVMTest {

    static final String LANG_DATO_FORMAT = "MMMM yyyy";
    private static final String ID = "id";

    @Test
    public void testOppsummertPeriode_AlleUtbetalingsdatoerISammeMaaned_DatoFormateringErMaaned() throws Exception {
        DateTime dato = new DateTime(2014, 1, 1, 1, 1);
        String formatertDato = dato.toString(LANG_DATO_FORMAT);
        List<Hovedytelse> utbetalinger = asList(getYtelse(dato));

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, dato.toLocalDate(), dato.toLocalDate(), dato.toLocalDate());
        String oppsummertPeriode = vm.getOppsummertPeriode();

        assertThat(oppsummertPeriode, is(formatertDato));
    }

    @Test
    public void testOppsummertPeriode_UtbetalingsdatoerIForskjelligeMaaneder_DatoFormateringErIntervall() throws Exception {
        DateTime dato = now().minusDays(1);
        LocalDate startDato = defaultStartDato();
        LocalDate sluttDato = defaultSluttDato();
        LocalDate visningSluttDato = defaultSluttDato();
        String formatertDato = Datoformat.kortUtenLiteral(startDato.toDateTimeAtStartOfDay()) + " - " +
                Datoformat.kortUtenLiteral(sluttDato.toDateTime(new LocalTime(23, 59)));
        List<Hovedytelse> utbetalinger = asList(getYtelse(dato));

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, startDato, sluttDato, visningSluttDato);
        String oppsummertPeriode = vm.getOppsummertPeriode();

        assertThat(oppsummertPeriode, is(formatertDato));
    }

    @Test
    public void testTransformer_LikeYtelser_BlirSlaattSammen() throws Exception {
        Underytelse ytelse1 = new Underytelse()
                .withYtelsesType("Grunnbeløp")
                .withSatsAntall(0d)
                .withYtelseBeloep(1000.0)
                .withSatsBeloep(0d);

        Underytelse ytelse2 = new Underytelse()
                .withYtelsesType("Tillegg")
                .withSatsAntall(0d)
                .withYtelseBeloep(500.0)
                .withSatsBeloep(0d);

        List<Double> skattTrekkListe = asList(-200.0);

        Hovedytelse dagpenger = new Hovedytelse()
                .withId(ID)
                .withYtelse("Dagpenger")
                .withHovedytelsedato(now())
                .withUtbetalingsDato(now())
                .withYtelsesperiode(new Interval(now(), now()))
                .withUnderytelseListe(asList(ytelse1))
                .withSumSkatt(skattTrekkListe.get(0))
                .withNettoUtbetalt(0d)
                .withSumTrekk(0d)
                .withBruttoUtbetalt(0d)
                .withSammenlagtTrekkBeloep()
                .withSkattListe(skattTrekkListe);

        Hovedytelse dagpenger1 = new Hovedytelse()
                .withId(ID)
                .withYtelse("Dagpenger")
                .withHovedytelsedato(now())
                .withUtbetalingsDato(now())
                .withYtelsesperiode(new Interval(now(), now()))
                .withUnderytelseListe(asList(ytelse2))
                .withSkattListe(skattTrekkListe)
                .withNettoUtbetalt(0d)
                .withSumTrekk(0d)
                .withBruttoUtbetalt(0d)
                .withSammenlagtTrekkBeloep()
                .withSumSkatt(skattTrekkListe.get(0));

        Hovedytelse dagpenger2 = new Hovedytelse()
                .withId(ID)
                .withYtelse("Helseprodukter")
                .withHovedytelsedato(now())
                .withUtbetalingsDato(now())
                .withNettoUtbetalt(0d)
                .withSumTrekk(0d)
                .withBruttoUtbetalt(0d)
                .withSammenlagtTrekkBeloep()
                .withYtelsesperiode(new Interval(now(), now()))
                .withUnderytelseListe(asList(ytelse2));

        List<Hovedytelse> hovedytelser = asList(dagpenger, dagpenger1, dagpenger2);

        OppsummeringVM vm = new OppsummeringVM(hovedytelser, defaultStartDato(), defaultSluttDato(), defaultVisningSluttDato());

        List<String> navn = asList("Grunnbeløp", "Tillegg", "Skatt");
        List<Double> belop = asList(1000.0, 500.0, -400.0);
        assertThat(vm.hovedytelser.size(), is(2));
        assertThat(vm.hovedytelser.get(0).getHovedYtelsesBeskrivelse(), is("Dagpenger"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().size(), is(2));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getBelop(), is(belop.get(0)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getUnderYtelsesBeskrivelse(), is(navn.get(0)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(1).getBelop(), is(belop.get(1)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(1).getUnderYtelsesBeskrivelse(), is(navn.get(1)));
        assertThat(vm.hovedytelser.get(1).getHovedYtelsesBeskrivelse(), is("Helseprodukter"));
        assertThat(vm.hovedytelser.get(1).getUnderYtelsesBeskrivelser().size(), is(1));
        assertThat(vm.hovedytelser.get(1).getUnderYtelsesBeskrivelser().get(0).getUnderYtelsesBeskrivelse(), is(navn.get(1)));
        assertThat(vm.hovedytelser.get(1).getUnderYtelsesBeskrivelser().get(0).getBelop(), is(belop.get(1)));
    }

    @Test
    public void testTransformer_LikeTitlerOgForskjelligeAntall_BlirSlaattSammen() throws Exception {

        Underytelse ytelse = new Underytelse()
                .withYtelsesType("Grønn")
                .withSatsAntall(1d)
                .withYtelseBeloep(100.0)
                .withSatsBeloep(0d);

        Underytelse ytelse2 = new Underytelse()
                .withYtelsesType("Grønn")
                .withSatsAntall(2d)
                .withYtelseBeloep(200.0)
                .withSatsBeloep(0d);

        Underytelse ytelse3 = new Underytelse()
                .withYtelsesType("Grønn")
                .withSatsAntall(3d)
                .withYtelseBeloep(300.0)
                .withSatsBeloep(0d);

        List<Underytelse> underytelser = asList(ytelse, ytelse2, ytelse3);
        Hovedytelse hovedytelse = new Hovedytelse()
                .withId(ID)
                .withYtelse("Våren")
                .withUnderytelseListe(underytelser)
                .withHovedytelsedato(now())
                .withUtbetalingsDato(now())
                .withNettoUtbetalt(0d)
                .withSumTrekk(0d)
                .withBruttoUtbetalt(0d)
                .withSammenlagtTrekkBeloep()
                .withYtelsesperiode(new Interval(now().minusDays(14), now()));

        List<Hovedytelse> utbetalinger = asList(hovedytelse);

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, defaultStartDato(), defaultSluttDato(), defaultVisningSluttDato());

        assertThat(vm.hovedytelser.size(), is(1));
        assertThat(vm.hovedytelser.get(0).getHovedYtelsesBeskrivelse(), is("Våren"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().size(), is(1));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getUnderYtelsesBeskrivelse(), is("Grønn"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getBelop(), is(600.0));
    }

    @Test
    public void summererTotalBruttoForAlleHovedytelser() {
        List<Hovedytelse> hovedytelser = new ArrayList<>();
        hovedytelser.add(getYtelse(DateTime.now()).withBruttoUtbetalt(1000.0));
        hovedytelser.add(getYtelse(DateTime.now()).withBruttoUtbetalt(200.0));
        hovedytelser.add(getYtelse(DateTime.now()).withBruttoUtbetalt(300.0));

        assertThat(OppsummeringVM.bruttoUtbetaltForAlle(hovedytelser), is(1500.0));
    }

    @Test
    public void summererTotalBruttoForHovedytelserMedNegativBrutto() {
        List<Hovedytelse> hovedytelser = new ArrayList<>();
        hovedytelser.add(getYtelse(DateTime.now()).withBruttoUtbetalt(1000.0));
        hovedytelser.add(getYtelse(DateTime.now()).withBruttoUtbetalt(-200.0));
        hovedytelser.add(getYtelse(DateTime.now()).withBruttoUtbetalt(-300.0));

        assertThat(OppsummeringVM.bruttoUtbetaltForAlle(hovedytelser), is(500.0));
    }

    @Test
    public void summererTotalNettoForAlleHovedytelser() {
        List<Hovedytelse> hovedytelser = new ArrayList<>();
        hovedytelser.add(getYtelse(DateTime.now()).withNettoUtbetalt(1000.0));
        hovedytelser.add(getYtelse(DateTime.now()).withNettoUtbetalt(200.0));
        hovedytelser.add(getYtelse(DateTime.now()).withNettoUtbetalt(300.0));

        assertThat(OppsummeringVM.nettoUtbetaltForAlle(hovedytelser), is(1500.0));
    }

    @Test
    public void summererTotalNettoForHovedytelserMedNegativBrutto() {
        List<Hovedytelse> hovedytelser = new ArrayList<>();
        hovedytelser.add(getYtelse(DateTime.now()).withNettoUtbetalt(1000.0));
        hovedytelser.add(getYtelse(DateTime.now()).withNettoUtbetalt(-200.0));
        hovedytelser.add(getYtelse(DateTime.now()).withNettoUtbetalt(-300.0));

        assertThat(OppsummeringVM.nettoUtbetaltForAlle(hovedytelser), is(500.0));
    }


    @Test
    public void summererTotalTrekkBeloepForAlleHovedytelser() {
        List<Hovedytelse> hovedytelser = new ArrayList<>();
        hovedytelser.add(getYtelse(DateTime.now()).withSumTrekk(1000.0).withSammenlagtTrekkBeloep());
        hovedytelser.add(getYtelse(DateTime.now()).withSumTrekk(200.0).withSammenlagtTrekkBeloep());
        hovedytelser.add(getYtelse(DateTime.now()).withSumTrekk(300.0).withSammenlagtTrekkBeloep());

        assertThat(OppsummeringVM.trekkBeloepForAlle(hovedytelser), is(1500.0));
    }

    @Test
    public void summererTotalTrekkBeloepForHovedytelserMedNegativBrutto() {
        List<Hovedytelse> hovedytelser = new ArrayList<>();
        hovedytelser.add(getYtelse(DateTime.now()).withSumTrekk(1000.0).withSammenlagtTrekkBeloep());
        hovedytelser.add(getYtelse(DateTime.now()).withSumTrekk(-200.0).withSammenlagtTrekkBeloep());
        hovedytelser.add(getYtelse(DateTime.now()).withSumTrekk(-300.0).withSammenlagtTrekkBeloep());

        assertThat(OppsummeringVM.trekkBeloepForAlle(hovedytelser), is(500.0));
    }

    @Test
    public void sjekkerAtBelopetPaaUnderytelseneIkkeBlirEndret() {
        List<Underytelse> underytelser = asList(
                new Underytelse()
                        .withYtelsesType("Sykpenger")
                        .withYtelseBeloep(1000.0),
                new Underytelse()
                        .withYtelsesType("Sykpenger")
                        .withYtelseBeloep(500.0)
        );

        Map<String, List<Underytelse>> indekserteUnderytelser = new HashMap<>();
        indekserteUnderytelser.put("sykepenger", underytelser);
        OppsummeringVM.combineUnderytelser(indekserteUnderytelser);


        assertThat(underytelser.get(0).getYtelseBeloep(), is(1000.0));
        assertThat(underytelser.get(1).getYtelseBeloep(), is(500.0));
    }

    @Test
    public void combineUnderytelserreturnererSummenAvIndekserteUnderytelsene() {
        List<Underytelse> underytelser = asList(
                new Underytelse()
                        .withYtelsesType("Sykpenger")
                        .withYtelseBeloep(1000.0),
                new Underytelse()
                        .withYtelsesType("Sykpenger")
                        .withYtelseBeloep(500.0)
        );

        Map<String, List<Underytelse>> indekserteUnderytelser = new HashMap<>();
        indekserteUnderytelser.put("sykepenger", underytelser);
        List<Underytelse> sammenlagteUnderytelser = OppsummeringVM.combineUnderytelser(indekserteUnderytelser);


        assertThat(sammenlagteUnderytelser.get(0).getYtelseBeloep(), is(1500.0));
    }

    @Test
    public void testKopieringAvUnderytelser() {
        Underytelse underytelse = new Underytelse()
                                        .withSatsAntall(1.0)
                                        .withSatsBeloep(10.0)
                                        .withSatsType("Satstype")
                                        .withYtelseBeloep(1000.0)
                                        .withYtelsesType("Ytelsestype");

        Underytelse underytelseKopi = OppsummeringVM.kopierUnderytelse(underytelse);

        assertThat(underytelse.getYtelseBeloep(), is(underytelseKopi.getYtelseBeloep()));
        assertThat(underytelse.getSatsAntall(), is(underytelseKopi.getSatsAntall()));
        assertThat(underytelse.getSatsBeloep(), is(underytelseKopi.getSatsBeloep()));
        assertThat(underytelse.getSatsType(), is(underytelseKopi.getSatsType()));
        assertThat(underytelse.getYtelsesType(), is(underytelseKopi.getYtelsesType()));

        assertThat(underytelse == underytelseKopi, is(false));

    }

    private Hovedytelse getYtelse(DateTime dato) {
        return new Hovedytelse()
                .withId(ID)
                .withYtelse("Kjeks")
                .withUnderytelseListe(asList(getUnderytelse()))
                .withHovedytelsedato(dato)
                .withNettoUtbetalt(0d)
                .withSumTrekk(0d)
                .withSammenlagtTrekkBeloep()
                .withBruttoUtbetalt(0d)
                .withYtelsesperiode(new Interval(dato.minusDays(14), dato));
    }

    private Underytelse getUnderytelse() {
        return new Underytelse()
                .withYtelsesType("UnderytelseType")
                .withYtelseBeloep(10d);
    }

}
