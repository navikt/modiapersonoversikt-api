package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultStartDato;
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
        List<Record<Hovedytelse>> utbetalinger = asList(getYtelse(dato));

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, dato.toLocalDate(), dato.toLocalDate());
        String oppsummertPeriode = vm.getOppsummertPeriode();

        assertThat(oppsummertPeriode, is(formatertDato));
    }

    @Test
    public void testOppsummertPeriode_UtbetalingsdatoerIForskjelligeMaaneder_DatoFormateringErIntervall() throws Exception {
        DateTime dato = now().minusDays(1);
        LocalDate startDato = defaultStartDato();
        LocalDate sluttDato = defaultSluttDato();
        String formatertDato = Datoformat.kortUtenLiteral(startDato.toDateTimeAtStartOfDay()) + " - " +
                Datoformat.kortUtenLiteral(sluttDato.toDateTime(new LocalTime(23, 59)));
        List<Record<Hovedytelse>> utbetalinger = asList(getYtelse(dato));

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, startDato, sluttDato);
        String oppsummertPeriode = vm.getOppsummertPeriode();

        assertThat(oppsummertPeriode, is(formatertDato));
    }

    @Test
    public void testTransformer_LikeYtelser_BlirSlaattSammen() throws Exception {
        Record<Underytelse> ytelse1 = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "Grunnbeløp")
                .with(Underytelse.satsAntall, 0d)
                .with(Underytelse.ytelseBeloep, 1000.0)
                .with(Underytelse.satsBeloep, 0d);

        Record<Underytelse> ytelse2 = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "Tillegg")
                .with(Underytelse.satsAntall, 0d)
                .with(Underytelse.ytelseBeloep, 500.0)
                .with(Underytelse.satsBeloep, 0d);

        //Skatt - 200

        List<Double> skattTrekkListe = asList(-200.0);

        Record<Hovedytelse> dagpenger = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Dagpenger")
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.ytelsesperiode, new Interval(now(), now()))
                .with(Hovedytelse.underytelseListe, asList(ytelse1))
                .with(Hovedytelse.sumSkatt, skattTrekkListe.get(0))
                .with(Hovedytelse.nettoUtbetalt, 0d)
                .with(Hovedytelse.sumTrekk, 0d)
                .with(Hovedytelse.bruttoUtbetalt, 0d)
                .with(Hovedytelse.sammenlagtTrekkBeloep, 0d)
                .with(Hovedytelse.skattListe, skattTrekkListe);

        Record<Hovedytelse> dagpenger1 = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Dagpenger")
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.ytelsesperiode, new Interval(now(), now()))
                .with(Hovedytelse.underytelseListe, asList(ytelse2))
                .with(Hovedytelse.skattListe, skattTrekkListe)
                .with(Hovedytelse.nettoUtbetalt, 0d)
                .with(Hovedytelse.sumTrekk, 0d)
                .with(Hovedytelse.bruttoUtbetalt, 0d)
                .with(Hovedytelse.sammenlagtTrekkBeloep, 0d)
                .with(Hovedytelse.sumSkatt, skattTrekkListe.get(0));

        Record<Hovedytelse> dagpenger2 = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Helseprodukter")
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.nettoUtbetalt, 0d)
                .with(Hovedytelse.sumTrekk, 0d)
                .with(Hovedytelse.bruttoUtbetalt, 0d)
                .with(Hovedytelse.sammenlagtTrekkBeloep, 0d)
                .with(Hovedytelse.ytelsesperiode, new Interval(now(), now()))
                .with(Hovedytelse.underytelseListe, asList(ytelse2));

        List<Record<Hovedytelse>> hovedytelser = asList(dagpenger, dagpenger1, dagpenger2);

        OppsummeringVM vm = new OppsummeringVM(hovedytelser, defaultStartDato(), defaultSluttDato());

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

        Record<Underytelse> ytelse = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "Grønn")
                .with(Underytelse.satsAntall, 1d)
                .with(Underytelse.ytelseBeloep, 100.0)
                .with(Underytelse.satsBeloep, 0d);

        Record<Underytelse> ytelse2 = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "Grønn")
                .with(Underytelse.satsAntall, 2d)
                .with(Underytelse.ytelseBeloep, 200.0)
                .with(Underytelse.satsBeloep, 0d);

        Record<Underytelse> ytelse3 = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "Grønn")
                .with(Underytelse.satsAntall, 3d)
                .with(Underytelse.ytelseBeloep, 300.0)
                .with(Underytelse.satsBeloep, 0d);

        List<Record<Underytelse>> underytelser = asList(ytelse, ytelse2, ytelse3);
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Våren")
                .with(Hovedytelse.underytelseListe, underytelser)
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.nettoUtbetalt, 0d)
                .with(Hovedytelse.sumTrekk, 0d)
                .with(Hovedytelse.bruttoUtbetalt, 0d)
                .with(Hovedytelse.sammenlagtTrekkBeloep, 0d)
                .with(Hovedytelse.ytelsesperiode, new Interval(now().minusDays(14), now()));

        List<Record<Hovedytelse>> utbetalinger = asList(hovedytelse);

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, defaultStartDato(), defaultSluttDato());

        assertThat(vm.hovedytelser.size(), is(1));
        assertThat(vm.hovedytelser.get(0).getHovedYtelsesBeskrivelse(), is("Våren"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().size(), is(1));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getUnderYtelsesBeskrivelse(), is("Grønn"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getBelop(), is(600.0));
    }

    private Record<Hovedytelse> getYtelse(DateTime dato) {
        return new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Kjeks")
                .with(Hovedytelse.underytelseListe, asList(getUnderytelse()))
                .with(Hovedytelse.hovedytelsedato, dato)
                .with(Hovedytelse.nettoUtbetalt, 0d)
                .with(Hovedytelse.sumTrekk, 0d)
                .with(Hovedytelse.sammenlagtTrekkBeloep, 0d)
                .with(Hovedytelse.bruttoUtbetalt, 0d)
                .with(Hovedytelse.ytelsesperiode, new Interval(dato.minusDays(14), dato));
    }

    private Record<Underytelse> getUnderytelse() {
        return new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "UnderytelseType")
                .with(Underytelse.ytelseBeloep, 10d);
    }

}
