package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class OppsummeringVMTest {
    static final String LANG_DATO_FORMAT = "MMMM yyyy";

    @Test
    public void testOppsummertPeriode_AlleUtbetalingsdatoerISammeMaaned_DatoFormateringErMaaned() throws Exception {
        DateTime dato = new DateTime(2014, 1, 1, 1, 1);
        String formatertDato = dato.toString(LANG_DATO_FORMAT);
        List<Utbetaling> utbetalinger = asList(getUtbetaling(dato));

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, dato.toLocalDate(), dato.toLocalDate());
        String oppsummertPeriode = vm.getOppsummertPeriode();

        assertThat(oppsummertPeriode, is(formatertDato));
    }

    @Test
    public void testOppsummertPeriode_UtbetalingsdatoerIForskjelligeMaaneder_DatoFormateringErIntervall() throws Exception {
        DateTime dato = DateTime.now().minusDays(1);
        LocalDate startDato = defaultStartDato();
        LocalDate sluttDato = defaultSluttDato();
        String formatertDato = KORT.transform(startDato.toDateTimeAtStartOfDay()) + " - " + KORT.transform(sluttDato.toDateTime(new LocalTime(23, 59)));
        List<Utbetaling> utbetalinger = asList(getUtbetaling(dato));

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, startDato, sluttDato);
        String oppsummertPeriode = vm.getOppsummertPeriode();

        assertThat(oppsummertPeriode, is(formatertDato));
    }

    @Test
    public void testTransformer_LikeYtelser_BlirSlaattSammen() throws Exception {
        Underytelse ytelse1 = new Underytelse("Grunnbeløp", "", 0, 1000.0, 0);
        Underytelse ytelse2 = new Underytelse("Tillegg", "", 0, 500.0, 0);
        Underytelse ytelse3 = new Underytelse("Skatt", "", 0, -200.0, 0);
        Utbetaling dagpenger = new UtbetalingBuilder().withHovedytelse("Dagpenger").withUnderytelser(asList(ytelse1, ytelse3)).createUtbetaling();
        Utbetaling dagpenger1 = new UtbetalingBuilder().withHovedytelse("Dagpenger").withUnderytelser(asList(ytelse2, ytelse3)).createUtbetaling();
        Utbetaling dagpenger2 = new UtbetalingBuilder().withHovedytelse("Helseprodukter").withUnderytelser(asList(ytelse2)).createUtbetaling();

        List<Utbetaling> utbetalinger = asList(dagpenger, dagpenger1, dagpenger2);

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, defaultStartDato(), defaultSluttDato());

        List<String> navn = asList("Grunnbeløp", "Tillegg", "Skatt");
        List<Double> belop = asList(1000.0, 500.0, -400.0);
        assertThat(vm.hovedytelser.size(), is(2));
        assertThat(vm.hovedytelser.get(0).getHovedYtelsesBeskrivelse(), is("Dagpenger"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().size(), is(3));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getBelop(), is(belop.get(0)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getUnderYtelsesBeskrivelse(), is(navn.get(0)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(1).getBelop(), is(belop.get(1)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(1).getUnderYtelsesBeskrivelse(), is(navn.get(1)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(2).getBelop(), is(belop.get(2)));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(2).getUnderYtelsesBeskrivelse(), is(navn.get(2)));
        assertThat(vm.hovedytelser.get(1).getHovedYtelsesBeskrivelse(), is("Helseprodukter"));
        assertThat(vm.hovedytelser.get(1).getUnderYtelsesBeskrivelser().size(), is(1));
        assertThat(vm.hovedytelser.get(1).getUnderYtelsesBeskrivelser().get(0).getUnderYtelsesBeskrivelse(), is(navn.get(1)));
        assertThat(vm.hovedytelser.get(1).getUnderYtelsesBeskrivelser().get(0).getBelop(), is(belop.get(1)));
    }

    @Test
    public void testTransformer_LikeTitlerOgForskjelligeAntall_BlirSlaattSammen() throws Exception {

        Underytelse ytelse1 = new Underytelse("Grønn", "", 1, 100.0, 0);
        Underytelse ytelse2 = new Underytelse("Grønn", "", 2, 200.0, 0);
        Underytelse ytelse3 = new Underytelse("Grønn", "", 3, 300.0, 0);
        List<Underytelse> underytelser = asList(ytelse1, ytelse2, ytelse3);
        Utbetaling utbetaling = new UtbetalingBuilder().withHovedytelse("Våren").withUnderytelser(underytelser).createUtbetaling();
        List<Utbetaling> utbetalinger = asList(utbetaling);

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, defaultStartDato(), defaultSluttDato());

        assertThat(vm.hovedytelser.size(), is(1));
        assertThat(vm.hovedytelser.get(0).getHovedYtelsesBeskrivelse(), is("Våren"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().size(), is(1));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getUnderYtelsesBeskrivelse(), is("Grønn"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().get(0).getBelop(), is(600.0));
    }

    private Utbetaling getUtbetaling(DateTime dato) {
        return new UtbetalingBuilder().withHovedytelse("Kjeks")
                .withUnderytelser(asList(new Underytelse("", "", 0, 0, 0)))
                .withUtbetalingsDato(dato)
                .createUtbetaling();
    }

}
