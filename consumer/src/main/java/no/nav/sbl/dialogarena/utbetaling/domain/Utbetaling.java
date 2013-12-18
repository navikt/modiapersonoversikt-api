package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Utbetaling implements Serializable {

    public static final String BRUKER = "bruker";
    public static final String ARBEIDSGIVER = "arbeidsgiver";

    public static LocalDate defaultStartDato() {
        return LocalDate.now().minusMonths(3);
    }
    public static LocalDate defaultSluttDato() {
        return LocalDate.now();
    }

    private DateTime utbetalingsDato;
    private Interval periode;
    private String status;
    private String mottaker;
    private String melding;
    private String hovedytelse;
    private String kontonr;
    private String valuta;
    private List<Underytelse> underytelser;

    private Utbetaling(){}

    public static UtbetalingBuilder getBuilder() {
        return new UtbetalingBuilder();
    }

    public DateTime getUtbetalingsDato() {
        return utbetalingsDato;
    }

    public Interval getPeriode() {
        return periode;
    }

    public String getStatus() {
        return status;
    }

    public String getMottaker() {
        return mottaker;
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

    public List<Underytelse> getUnderytelser() {
        return underytelser;
    }

    public static class UtbetalingBuilder {
        private DateTime utbetalingsDato;
        private Interval periode;
        private String status;
        private String mottaker;
        private String melding;
        private String hovedytelse;
        private String kontonr;
        private String valuta;
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

        public UtbetalingBuilder withMottaker(String mottaker) {
            this.mottaker = mottaker;
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
            this.underytelser = underytelser;
            return this;
        }

        public Utbetaling createUtbetaling() {
            Utbetaling utbetaling = new Utbetaling();
            utbetaling.hovedytelse = this.hovedytelse;
            utbetaling.utbetalingsDato = this.utbetalingsDato;
            utbetaling.kontonr = this.kontonr;
            utbetaling.melding = this.melding;
            utbetaling.mottaker = this.mottaker;
            utbetaling.periode = this.periode;
            utbetaling.status = this.status;
            utbetaling.valuta = this.valuta;
            utbetaling.underytelser = this.underytelser;
            return utbetaling;
        }
    }
}
