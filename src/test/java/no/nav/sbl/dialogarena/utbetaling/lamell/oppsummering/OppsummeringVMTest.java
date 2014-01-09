package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Locale.forLanguageTag;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;


public class OppsummeringVMTest {
    static final String langDatoFormat = "MMMM yyyy";

    @Test
    public void testOppsummertPeriode_AlleUtbetalingsdatoerISammeMaaned_DatoFormateringErMaaned() throws Exception {
        DateTime dato = new DateTime(2014, 1, 1, 1 ,1);
        String formatertDato = dato.toString(langDatoFormat);
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
        String formatertDato = KORT.transform(startDato.toDateTimeAtStartOfDay()) + " - " + KORT.transform(sluttDato.toDateTime(new LocalTime(23,59)));
        List<Utbetaling> utbetalinger = asList(getUtbetaling(dato));

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, startDato, sluttDato);
        String oppsummertPeriode = vm.getOppsummertPeriode();

        assertThat(oppsummertPeriode, is(formatertDato));
    }

    @Test
    public void testTransformer_LikeYtelser_BlirSlaattSammen() throws Exception {
        Underytelse ytelse1 = new UnderytelseBuilder().setTittel("Grunnbeløp").setBelop(1000.0).createUnderytelse();
        Underytelse ytelse2 = new UnderytelseBuilder().setTittel("Tillegg").setBelop(500.0).createUnderytelse();
        Underytelse ytelse3 = new UnderytelseBuilder().setTittel("Skatt").setBelop(-200.0).createUnderytelse();
        Utbetaling dagpenger = new UtbetalingBuilder().withHovedytelse("Dagpenger").withUnderytelser(asList(ytelse1, ytelse3)).createUtbetaling();
        Utbetaling dagpenger1 = new UtbetalingBuilder().withHovedytelse("Dagpenger").withUnderytelser(asList(ytelse2, ytelse3)).createUtbetaling();
        Utbetaling dagpenger2 = new UtbetalingBuilder().withHovedytelse("Helseprodukter").withUnderytelser(asList(ytelse2)).createUtbetaling();

        List<Utbetaling> utbetalinger = asList(dagpenger, dagpenger1, dagpenger2);

        OppsummeringVM vm = new OppsummeringVM(utbetalinger, defaultStartDato(), defaultSluttDato());

        assertThat(vm.hovedytelser.size(), is(2));
        assertThat(vm.hovedytelser.get(0).getHovedYtelsesBeskrivelse(), is("Dagpenger"));
        assertThat(vm.hovedytelser.get(0).getUnderYtelsesBeskrivelser().size(), is(3));
        assertThat(vm.hovedytelser.get(1).getHovedYtelsesBeskrivelse(), is("Helseprodukter"));
        assertThat(vm.hovedytelser.get(1).getUnderYtelsesBeskrivelser().size(), is(1));
    }


    @Test
    public void testTransformer_LikeTitlerOgForskjelligeAntall_BlirSlaattSammen() throws Exception {
        Underytelse ytelse1 = new UnderytelseBuilder().setTittel("Grønn").setBelop(100.0).setAntall(1).createUnderytelse();
        Underytelse ytelse2 = new UnderytelseBuilder().setTittel("Grønn").setBelop(200.0).setAntall(2).createUnderytelse();
        Underytelse ytelse3 = new UnderytelseBuilder().setTittel("Grønn").setBelop(300.0).setAntall(3).createUnderytelse();
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
                .withUnderytelser(asList(new UnderytelseBuilder().createUnderytelse()))
                .withUtbetalingsDato(dato)
                .createUtbetaling();
    }

}
