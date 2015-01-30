package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;
import java.util.UUID;

public interface Hovedytelse {
    public Key<UUID> id = new Key<>("ID");

    public Key<DateTime> posteringsdato = new Key<>("POSTERINGS_DATO");
    public Key<Record<Aktoer>> utbetaltTil = new Key<>("UTBETALT_TIL");
    public Key<Mottakertype> mottakertype = new Key<>("MOTTAKER_TYPE");
    public Key<String> utbetalingsmelding = new Key<>("UTBETALING_MELDING");
    public Key<DateTime> utbetalingsDato = new Key<>("UTBETALING_DATO");
    public Key<DateTime> forfallsDato = new Key<>("FORFALL_DATO");
    public Key<Record<Konto>> utbetaltTilKonto = new Key<>("UTBETALT_TIL_KONTO");
    public Key<String> utbetalingsmetode = new Key<>("UTBETALING_METODE");
    public Key<String> utbetalingsstatus = new Key<>("UTBETALING_STATUS");

    public Key<String> ytelse = new Key<>("YTELSE");
    public Key<Interval> ytelsesperiode = new Key<>("YTELSE_PERIODE");
    public Key<DateTime> ytelseperiode_start = new Key<>("YTELSE_PERIODE_START");
    public Key<List<Record<Underytelse>>> underytelseListe = new Key<>("UNDERYTELSE_LISTE");
    public Key<Double> sumUnderytelser = new Key<>("SUM_UNDERYTELSER");
    public Key<List<Record<Trekk>>> trekkListe = new Key<>("TREKK_LISTE");
    public Key<Double> sumTrekk = new Key<>("SUM_TREKK");
    public Key<List<Double>> skattListe = new Key<>("SKATT_LISTE");
    public Key<Double> sumSkatt = new Key<>("SUM_SKATT");
    public Key<Double> ytelseNettoBeloep = new Key<>("YTELSE_NETTO_BELOEP");
    public Key<Double> ytelseBruttoBeloep = new Key<>("YTELSE_BRUTTO_BELOEP");
    public Key<String> bilagsnummer = new Key<>("BILAGSNUMMER");
    public Key<Record<Aktoer>> rettighetshaver = new Key<>("RETTIGHETSHAVER");
    public Key<Record<Aktoer>> refundertForOrg = new Key<>("REFUNDERT_FOR_ORG");
}
