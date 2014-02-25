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
        BRUKER, ANNEN_MOTTAKER
    }

    private String id;
    private DateTime utbetalingsdato;
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
    private List<Underytelse> underytelser;
    private Utbetaling() {}

    public static LocalDate defaultStartDato() {
        return now().minusMonths(3);
    }

    public static LocalDate defaultSluttDato() {
        return now();
    }

    public static UtbetalingBuilder getBuilder(String id) {
        return new UtbetalingBuilder(id);
    }

    public String getUtbetalingId() {
        return id;
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

    public Mottaktertype getMottakertype() {
        return mottakertype;
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

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Utbetaling && this.id.equals(((Utbetaling) obj).id);
    }

    public static class UtbetalingBuilder {

        private Utbetaling utbetaling;

        public UtbetalingBuilder(String id) {
            this.utbetaling = new Utbetaling();
            this.utbetaling.id = id;
            this.utbetaling.underytelser = new ArrayList<>();
        }

        public UtbetalingBuilder withUtbetalingsDato(DateTime utbetalingsdato) {
            this.utbetaling.utbetalingsdato = utbetalingsdato;
            return this;
        }

        public UtbetalingBuilder withPeriode(Interval periode) {
            this.utbetaling.periode = periode;
            return this;
        }

        public UtbetalingBuilder withStatus(String status) {
            this.utbetaling.status = status;
            return this;
        }

        public UtbetalingBuilder withMottakerId(String mottakerId) {
            this.utbetaling.mottakerId = mottakerId;
            return this;
        }

        public UtbetalingBuilder withMottakernavn(String mottakernavn) {
            this.utbetaling.mottakernavn = mottakernavn;
            return this;
        }

        public UtbetalingBuilder withMottakertype(Mottaktertype mottakertype) {
            this.utbetaling.mottakertype = mottakertype;
            return this;
        }

        public UtbetalingBuilder withMelding(String melding) {
            this.utbetaling.melding = melding;
            return this;
        }

        public UtbetalingBuilder withHovedytelse(String hovedytelse) {
            this.utbetaling.hovedytelse = hovedytelse;
            return this;
        }

        public UtbetalingBuilder withKontonr(String kontonr) {
            this.utbetaling.kontonr = kontonr;
            return this;
        }

        public UtbetalingBuilder withValuta(String valuta) {
            this.utbetaling.valuta = valuta;
            return this;
        }

        public UtbetalingBuilder withUnderytelser(List<Underytelse> underytelser) {
            this.utbetaling.underytelser.addAll(underytelser);
            return this;
        }

        public UtbetalingBuilder withBrutto(double brutto) {
            this.utbetaling.brutto = brutto;
            return this;
        }

        public UtbetalingBuilder withTrekk(double trekk) {
            this.utbetaling.trekk = trekk;
            return this;
        }

        public UtbetalingBuilder withUtbetalt(double utbetalt) {
            this.utbetaling.utbetalt = utbetalt;
            return this;
        }

        public Utbetaling build() {
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

    public static final Transformer<Utbetaling, Interval> PERIODE = new Transformer<Utbetaling, Interval>() {
        @Override
        public Interval transform(Utbetaling utbetaling) {
            return utbetaling.getPeriode();
        }
    };

}
