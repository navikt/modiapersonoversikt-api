package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.Comparator;


final class UtbetalingTransformObjekt {
    private DateTime utbetalingsDato;

    private String mottaker;
    private String mottakerId;
    private String kontonummer;
    private String status;

    private String spesifikasjon;

    private String melding;
    private String hovedYtelse;
    private String underYtelse;
    private Double belop;
    private Interval periode;
    private int antall;
    private Double sats;
    private String valuta;
    private UtbetalingTransformObjekt() {
    }

    public static UtbetalingTransformObjektBuilder getBuilder() {
        return new UtbetalingTransformObjektBuilder();
    }

    public static final Transformer<UtbetalingTransformObjekt, DateTime> UTBETALINGS_DATO = new Transformer<UtbetalingTransformObjekt, DateTime>() {
        @Override
        public DateTime transform(UtbetalingTransformObjekt utbetalingTransformObjekt) {
            return utbetalingTransformObjekt.getUtbetalingsDato();
        }
    };

    public static final Transformer<UtbetalingTransformObjekt, LocalDate> UTBETALINGS_DAG = new Transformer<UtbetalingTransformObjekt, LocalDate>() {
        @Override
        public LocalDate transform(UtbetalingTransformObjekt utbetalingTransformObjekt) {
            return utbetalingTransformObjekt.getUtbetalingsDato().toLocalDate();
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UtbetalingTransformObjekt)) return false;

        UtbetalingTransformObjekt that = (UtbetalingTransformObjekt) o;

        if (hovedYtelse != null ? !hovedYtelse.equals(that.hovedYtelse) : that.hovedYtelse != null) return false;
        if (kontonummer != null ? !kontonummer.equals(that.kontonummer) : that.kontonummer != null) return false;
        if (mottaker != null ? !mottaker.equals(that.mottaker) : that.mottaker != null) return false;
        if (mottakerId != null ? !mottakerId.equals(that.mottakerId) : that.mottakerId != null) return false;
        if (periode != null ? !periode.equals(that.periode) : that.periode != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (utbetalingsDato != null ? !utbetalingsDato.equals(that.utbetalingsDato) : that.utbetalingsDato != null)
            return false;
        if (valuta != null ? !valuta.equals(that.valuta) : that.valuta != null) return false;
        if (spesifikasjon != null ? !spesifikasjon.equals(that.spesifikasjon) : that.spesifikasjon != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = utbetalingsDato != null ? utbetalingsDato.hashCode() : 0;
        result = 31 * result + (mottaker != null ? mottaker.hashCode() : 0);
        result = 31 * result + (mottakerId != null ? mottakerId.hashCode() : 0);
        result = 31 * result + (kontonummer != null ? kontonummer.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (hovedYtelse != null ? hovedYtelse.hashCode() : 0);
        result = 31 * result + (periode != null ? periode.hashCode() : 0);
        result = 31 * result + (valuta != null ? valuta.hashCode() : 0);
        return result;
    }

    public static final class TransformComparator {

        public static Comparator<UtbetalingTransformObjekt> DATO = new Comparator<UtbetalingTransformObjekt>() {
            @Override
            public int compare(UtbetalingTransformObjekt o1, UtbetalingTransformObjekt o2) {
                return o1.getUtbetalingsDato().compareTo(o2.getUtbetalingsDato());
            }
        };
    }
    public int getAntall() {
        return antall;
    }


    public Double getBelop() {
        return belop;
    }

    public void setBelop(Double belop) {
        this.belop = belop;
    }

    public String getSpesifikasjon() {
        return spesifikasjon;
    }

    public Interval getPeriode() {
        return periode;
    }

    public String getHovedYtelse() {
        return hovedYtelse;
    }

    public String getKontonummer() {
        return kontonummer;
    }

    public String getMelding() {
        return melding;
    }

    public String getMottaker() {
        return mottaker;
    }

    public String getMottakerId() {
        return mottakerId;
    }

    public Double getSats() {
        return sats;
    }

    public String getStatus() {
        return status;
    }

    public String getUnderYtelse() {
        return underYtelse;
    }

    //private String kildeSystem;
    public void setMelding(String melding) {
        this.melding = melding;
    }

    public DateTime getUtbetalingsDato() {
        return utbetalingsDato;
    }

    public String getValuta() {
        return valuta;
    }

    static final class UtbetalingTransformObjektBuilder {
        private int antall;
        private Double belop;
        private String hovedYtelse;
        private String kontonummer;
        private String mottaker;
        private String mottakerId;
        private Double sats;
        private String status;
        private String underYtelse;
        private DateTime utbetalingsDato;
        private Interval periode;
        private String valuta;

        private String spesifikasjon;

        public UtbetalingTransformObjektBuilder withSpesifikasjon(String spesifikasjon) {
            this.spesifikasjon = spesifikasjon;
            return this;
        }

        public UtbetalingTransformObjektBuilder withAntall(int antall) {
            this.antall = antall;
            return this;
        }

        public UtbetalingTransformObjektBuilder withBelop(Double belop) {
            this.belop = belop;
            return this;
        }

        public UtbetalingTransformObjektBuilder withHovedYtelse(String hovedYtelse) {
            this.hovedYtelse = hovedYtelse;
            return this;
        }

        public UtbetalingTransformObjektBuilder withKontonummer(String kontonummer) {
            this.kontonummer = kontonummer;
            return this;
        }

        public UtbetalingTransformObjektBuilder withMottaker(String mottaker) {
            this.mottaker = mottaker;
            return this;
        }

        public UtbetalingTransformObjektBuilder withMottakerId(String mottakerId) {
            this.mottakerId = mottakerId;
            return this;
        }

        public UtbetalingTransformObjektBuilder withSats(Double sats) {
            this.sats = sats;
            return this;
        }

        public UtbetalingTransformObjektBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public UtbetalingTransformObjektBuilder withUnderYtelse(String underYtelse) {
            this.underYtelse = underYtelse;
            return this;
        }

        public UtbetalingTransformObjektBuilder withUtbetalingsDato(DateTime utbetalingsDato) {
            this.utbetalingsDato = utbetalingsDato;
            return this;
        }

        public UtbetalingTransformObjektBuilder withPeriode(Interval periode) {
            this.periode = periode;
            return this;
        }

        public UtbetalingTransformObjektBuilder withValuta(String valuta) {
            this.valuta = valuta;
            return this;
        }

        public UtbetalingTransformObjekt build() {
            UtbetalingTransformObjekt transformObjekt = new UtbetalingTransformObjekt();
            transformObjekt.antall = this.antall;
            transformObjekt.belop = this.belop;
            transformObjekt.hovedYtelse = this.hovedYtelse;
            transformObjekt.kontonummer = this.kontonummer;
            transformObjekt.mottaker = this.mottaker;
            transformObjekt.mottakerId = this.mottakerId;
            transformObjekt.sats = this.sats;
            transformObjekt.status = this.status;
            transformObjekt.underYtelse = this.underYtelse;
            transformObjekt.utbetalingsDato = this.utbetalingsDato;
            transformObjekt.periode = this.periode;
            transformObjekt.valuta = this.valuta;
            transformObjekt.spesifikasjon = this.spesifikasjon;
            return transformObjekt;
        }
    }
}
