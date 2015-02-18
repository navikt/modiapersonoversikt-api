package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

@SuppressWarnings("all")
public interface Hovedytelse {
    Key<String> id = new Key<>("ID");

    Key<DateTime> hovedytelsedato = new Key<>("POSTERINGS_DATO");
    Key<DateTime> forfallsdato = new Key<>("FORFALLS_DATO");
    Key<Record<Aktoer>> utbetaltTil = new Key<>("UTBETALT_TIL");
    Key<Mottakertype> mottakertype = new Key<>("MOTTAKER_TYPE");
    Key<String> utbetalingsmelding = new Key<>("UTBETALING_MELDING");
    Key<Record<Konto>> utbetaltTilKonto = new Key<>("UTBETALT_TIL_KONTO");
    Key<String> utbetalingsmetode = new Key<>("UTBETALING_METODE");
    Key<String> utbetalingsstatus = new Key<>("UTBETALING_STATUS");

    Key<String> ytelse = new Key<>("YTELSE");
    Key<Interval> ytelsesperiode = new Key<>("YTELSE_PERIODE");
    Key<List<Record<Underytelse>>> underytelseListe = new Key<>("UNDERYTELSE_LISTE");
    Key<List<Record<Trekk>>> trekkListe = new Key<>("TREKK_LISTE");
    Key<Double> sumTrekk = new Key<>("SUM_TREKK");
    Key<List<Double>> skattListe = new Key<>("SKATT_LISTE");
    Key<Double> sumSkatt = new Key<>("SUM_SKATT");
    Key<Double> nettoUtbetalt = new Key<>("YTELSE_NETTO_BELOEP");
    Key<String> bilagsnummer = new Key<>("BILAGSNUMMER");
    Key<Record<Aktoer>> rettighetshaver = new Key<>("RETTIGHETSHAVER");
    Key<Record<Aktoer>> refundertForOrg = new Key<>("REFUNDERT_FOR_ORG");

    Key<Double> bruttoUtbetalt = new Key<>("AGGREGERT_BRUTTO_BELOEP");
    Key<Double> sammenlagtTrekkBeloep = new Key<>("AGGREGERT_TREKK_BELOEP");
}
