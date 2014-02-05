package no.nav.sbl.dialogarena.utbetaling.domain;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.joda.time.LocalDate.now;

public final class Utbetaling implements Serializable {

    public static enum Mottaktertype {
        BRUKER, ANNEN_MOTTAKER;
    }

    private DateTime utbetalingsdato;
    private Interval periode;
    private String status;
    private String mottakerId;
    private String mottakernavn;
    private Mottaktertype mottaktertype;
    private String melding;
    private String hovedytelse;
    private String kontonr;
    private String valuta;
    private double brutto;
    private double trekk;
    private double utbetalt;
    private List<Underytelse> underytelser;
    private Utbetaling() {}

    public static LocalDate defaultStartDato() {
        return now().minusMonths(3);
    }

    public static LocalDate defaultSluttDato() {
        return now();
    }

    public static UtbetalingBuilder getBuilder() {
        return new UtbetalingBuilder();
    }

    /**
     * Minste felles multiplum for å unikt identifisere en betaling er
     * dato, mottaker og hovedytelse.
     */
    public String getUtbetalingId() {
        return ("" +
                utbetalingsdato.getDayOfMonth() +
                utbetalingsdato.getMonthOfYear() +
                utbetalingsdato.getYear() +
                mottakerId +
                hovedytelse)
                .replace(" ", "");
    }

    public DateTime getUtbetalingsdato() {
        return utbetalingsdato;
    }

    public Interval getPeriode() {
        return periode;
    }

    public String getStatus() {
        return status;
    }

    public String getMottakerId() {
        return mottakerId;
    }

    public String getMottakernavn() {
        return mottakernavn;
    }

    public Mottaktertype getMottaktertype() {
        return mottaktertype;
    }

    public String getMelding() {
        return melding;
    }

    public String getHovedytelse() {
        return hovedytelse;
    }

    public String getKontonr() {
        return kontonr;
    }

    public String getValuta() {
        return valuta;
    }

    public double getBrutto() {
        return brutto;
    }

    public double getTrekk() {
        return trekk;
    }

    public double getUtbetalt() {
        return utbetalt;
    }

    public List<Underytelse> getUnderytelser() {
        return underytelser;
    }

    public static class UtbetalingBuilder {
        private DateTime utbetalingsDato;
        private Interval periode;
        private String status;
        private String mottakerId;
        private String mottakernavn;
        private Mottaktertype mottakertype;
        private String melding;
        private String hovedytelse;
        private String kontonr;
        private String valuta;
        private double brutto;
        private double trekk;
        private double utbetalt;
        private List<Underytelse> underytelser = new ArrayList<>();

        public UtbetalingBuilder withUtbetalingsDato(DateTime utbetalingsDato) {
            this.utbetalingsDato = utbetalingsDato;
            return this;
        }

        public UtbetalingBuilder withPeriode(Interval periode) {
            this.periode = periode;
            return this;
        }

        public UtbetalingBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public UtbetalingBuilder withMottakerId(String mottakerId) {
            this.mottakerId = mottakerId;
            return this;
        }

        public UtbetalingBuilder withMottakernavn(String mottakernavn) {
            this.mottakernavn = mottakernavn;
            return this;
        }

        public UtbetalingBuilder withMottakertype(Mottaktertype mottakertype) {
            this.mottakertype = mottakertype;
            return this;
        }

        public UtbetalingBuilder withMelding(String melding) {
            this.melding = melding;
            return this;
        }

        public UtbetalingBuilder withHovedytelse(String hovedytelse) {
            this.hovedytelse = hovedytelse;
            return this;
        }

        public UtbetalingBuilder withKontonr(String kontonr) {
            this.kontonr = kontonr;
            return this;
        }

        public UtbetalingBuilder withValuta(String valuta) {
            this.valuta = valuta;
            return this;
        }

        public UtbetalingBuilder withUnderytelser(List<Underytelse> underytelser) {
            this.underytelser.addAll(underytelser);
            return this;
        }

        public UtbetalingBuilder withBrutto(double brutto) {
            this.brutto = brutto;
            return this;
        }

        public UtbetalingBuilder withTrekk(double trekk) {
            this.trekk = trekk;
            return this;
        }

        public UtbetalingBuilder withUtbetalt(double utbetalt) {
            this.utbetalt = utbetalt;
            return this;
        }

        public Utbetaling createUtbetaling() {
            Utbetaling utbetaling = new Utbetaling();
            utbetaling.utbetalingsdato = this.utbetalingsDato;
            utbetaling.periode = this.periode;
            utbetaling.status = this.status;
            utbetaling.mottakerId = this.mottakerId;
            utbetaling.mottakernavn = this.mottakernavn;
            utbetaling.mottaktertype = this.mottakertype;
            utbetaling.melding = this.melding;
            utbetaling.hovedytelse = this.hovedytelse;
            utbetaling.kontonr = this.kontonr;
            utbetaling.valuta = this.valuta;
            utbetaling.brutto = this.brutto;
            utbetaling.trekk = this.trekk;
            utbetaling.utbetalt = this.utbetalt;
            utbetaling.underytelser = this.underytelser;
            return utbetaling;
        }
    }

    public static final class UtbetalingComparator {
        public static final Comparator<Utbetaling> UTBETALING_DAG_YTELSE = new Comparator<Utbetaling>() {
            @Override
            public int compare(Utbetaling o1, Utbetaling o2) {
                int compareDato = -o1.getUtbetalingsdato().toLocalDate().compareTo(o2.getUtbetalingsdato().toLocalDate());
                if (compareDato == 0) {
                    return o1.getHovedytelse().compareToIgnoreCase(o2.getHovedytelse());
                }
                return compareDato;
            }
        };
    }

    public static final Transformer<Utbetaling, String> HOVEDYTELSE = new Transformer<Utbetaling, String>() {
        @Override
        public String transform(Utbetaling utbetaling) {
            return utbetaling.getHovedytelse();
        }
    };

    public static final Transformer<Utbetaling, List<Underytelse>> UNDERYTELSER = new Transformer<Utbetaling, List<Underytelse>>() {
        @Override
        public List<Underytelse> transform(Utbetaling utbetaling) {
            return utbetaling.getUnderytelser();
        }
    };

}
