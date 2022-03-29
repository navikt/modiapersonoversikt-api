package no.nav.modiapersonoversikt.consumer.infotrygd.domain;

import no.nav.modiapersonoversikt.commondomain.Periode;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class HistoriskUtbetalingTest {

    public static final LocalDate PERIODEFRA = new LocalDate(2013, 8, 6);
    public static final LocalDate PERIODETOM = new LocalDate(2014, 2, 11);
    public static final LocalDate UTBETALINGSDATO = new LocalDate(2012, 7, 11);
    public static final Double HISTORTISK_UTBETALING_NETTOBELOP = 200.55;
    public static final Double HISTORTISK_UTBETALING_BRUTTOBELOP = 220.55;
    public static final Double HISTORTISK_UTBETALING_DAGSATS = 120.89;
    public static final Double HISTORTISK_UTBETALING_SKATTETREKK = 100.20;
    public static final Double HISTORTISK_UTBETALING_GRAD = 0.80;
    public static final Double HISTORTISK_UTBETALING_KREDITORTREKK_BELOP = 2000.60;
    public static final String HISTORTISK_UTBETALING_KREDITORTREKK_NAVN = "Bank Norwegian";
    public static final String ARBEIDSGIVER_ORGNR = "1234.12.7845";
    public static final String ARBEIDSGIVER_NAVN = "Statoil";

    @Test
    public void testBean() {
        HistoriskUtbetaling historiskUtbetaling = new HistoriskUtbetaling();
        historiskUtbetaling.setBruttobeloep(HISTORTISK_UTBETALING_BRUTTOBELOP);
        historiskUtbetaling.setNettobelop(HISTORTISK_UTBETALING_NETTOBELOP);
        historiskUtbetaling.setSkattetrekk(HISTORTISK_UTBETALING_SKATTETREKK);
        historiskUtbetaling.setUtbetalingsdato(UTBETALINGSDATO);
        historiskUtbetaling.setTrekk(new ArrayList<Kreditortrekk>());
        historiskUtbetaling.getTrekk().add(new Kreditortrekk(HISTORTISK_UTBETALING_KREDITORTREKK_NAVN, HISTORTISK_UTBETALING_KREDITORTREKK_BELOP));
        historiskUtbetaling.setArbeidsgiverNavn(ARBEIDSGIVER_NAVN);
        historiskUtbetaling.setArbeidsgiverOrgNr(ARBEIDSGIVER_ORGNR);
        historiskUtbetaling.setDagsats(HISTORTISK_UTBETALING_DAGSATS);
        historiskUtbetaling.setUtbetalingsgrad(HISTORTISK_UTBETALING_GRAD);
        Periode periode = new Periode();
        periode.setTo(PERIODETOM);
        periode.setFrom(PERIODEFRA);
        historiskUtbetaling.setVedtak(periode);

        assertEquals(HISTORTISK_UTBETALING_BRUTTOBELOP, historiskUtbetaling.getBruttobeloep());
        assertEquals(HISTORTISK_UTBETALING_NETTOBELOP, historiskUtbetaling.getNettobelop());
        assertEquals(HISTORTISK_UTBETALING_SKATTETREKK, historiskUtbetaling.getSkattetrekk());
        assertEquals(UTBETALINGSDATO, historiskUtbetaling.getUtbetalingsdato());
        assertEquals(HISTORTISK_UTBETALING_KREDITORTREKK_NAVN, historiskUtbetaling.getTrekk().get(0).getKreditorsNavn());
        assertEquals(HISTORTISK_UTBETALING_KREDITORTREKK_BELOP, historiskUtbetaling.getTrekk().get(0).getBelop());
        assertEquals(ARBEIDSGIVER_NAVN, historiskUtbetaling.getArbeidsgiverNavn());
        assertEquals(ARBEIDSGIVER_ORGNR, historiskUtbetaling.getArbeidsgiverOrgNr());
        assertEquals(HISTORTISK_UTBETALING_DAGSATS, historiskUtbetaling.getDagsats());
        assertEquals(HISTORTISK_UTBETALING_GRAD, historiskUtbetaling.getUtbetalingsgrad());
        assertEquals(PERIODEFRA, historiskUtbetaling.getVedtak().getFrom());
        assertEquals(PERIODETOM, historiskUtbetaling.getVedtak().getTo());
    }
}
