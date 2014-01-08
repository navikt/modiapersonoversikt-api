package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator.MERGEABLE_TITTEL;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator.MERGEABLE_TITTEL_ANTALL_SATS;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.getBrutto;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.getTrekk;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.MergeUtil.merge;
import static org.apache.commons.lang3.StringUtils.join;


final class UtbetalingTransformObjekt implements Mergeable {
    private DateTime utbetalingsdato;

    private String mottaker;
    private String mottakerId;
    private String mottakerKode;
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

    public static final Transformer<UtbetalingTransformObjekt, Double> BELOP = new Transformer<UtbetalingTransformObjekt, Double>() {
        @Override
        public Double transform(UtbetalingTransformObjekt utbetalingTransformObjekt) {
            return utbetalingTransformObjekt.getBelop();
        }
    };

    public static final Transformer<UtbetalingTransformObjekt, String> MELDING = new Transformer<UtbetalingTransformObjekt, String>() {
        @Override
        public String transform(UtbetalingTransformObjekt utbetalingTransformObjekt) {
            return utbetalingTransformObjekt.getMelding();
        }
    };

    public static final Transformer<Mergeable, UtbetalingTransformObjekt> MERGEABLE_TRANSFORMER = new Transformer<Mergeable, UtbetalingTransformObjekt>() {
        @Override
        public UtbetalingTransformObjekt transform(Mergeable objekt) {
            return (UtbetalingTransformObjekt) objekt;
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtbetalingTransformObjekt)) {
            return false;
        }

        UtbetalingTransformObjekt that = (UtbetalingTransformObjekt) o;

        LocalDate utbDato = utbetalingsdato != null? utbetalingsdato.toLocalDate() : new LocalDate();
        LocalDate thatUtbDato = that.utbetalingsdato != null? that.utbetalingsdato.toLocalDate() : utbDato;
        return new EqualsBuilder()
                .append(utbDato, thatUtbDato)
                .append(hovedYtelse, that.hovedYtelse)
                .append(kontonummer, that.kontonummer)
                .append(mottaker, that.mottaker)
                .append(mottakerId, that.mottakerId)
                .append(mottakerKode, that.mottakerKode)
                .append(periode, that.periode)
                .append(status, that.status)
                .append(valuta, that.valuta)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = utbetalingsdato != null ? utbetalingsdato.toLocalDate().hashCode() : 0;
        result = 31 * result + (mottaker != null ? mottaker.hashCode() : 0);
        result = 31 * result + (mottakerId != null ? mottakerId.hashCode() : 0);
        result = 31 * result + (mottakerKode != null ? mottakerKode.hashCode() : 0);
        result = 31 * result + (kontonummer != null ? kontonummer.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (hovedYtelse != null ? hovedYtelse.hashCode() : 0);
        result = 31 * result + (periode != null ? periode.hashCode() : 0);
        result = 31 * result + (valuta != null ? valuta.hashCode() : 0);
        return result;
    }

    @Override
    public Utbetaling doMerge(List<Mergeable> merges) {
        List<UtbetalingTransformObjekt> skalMerges = on(merges).map(MERGEABLE_TRANSFORMER).collectIn(new ArrayList<UtbetalingTransformObjekt>());
        if(skalMerges.isEmpty()) { return null; }

        Utbetaling.UtbetalingBuilder utbetalingBuilder = lagUtbetalingBuilder(skalMerges.get(0));

        Set<String> meldinger = on(skalMerges).map(UtbetalingTransformObjekt.MELDING).collectIn(new HashSet<String>());
        String melding = join(meldinger, ". ");

        // hent underytelser
        List<Underytelse> underytelser = new ArrayList<>();
        for (UtbetalingTransformObjekt objekt : skalMerges) {
            underytelser.add(new Underytelse(objekt.getUnderYtelse(), objekt.getSpesifikasjon(), objekt.getAntall(), objekt.getBelop(), objekt.getSats()));
        }

        LinkedList<Underytelse> list = new LinkedList<>(underytelser);
        leggSammenBelop(utbetalingBuilder, underytelser);

        List<Underytelse> sammenlagteUnderytelser = merge(new ArrayList<Mergeable>(list),
                                                                              MERGEABLE_TITTEL_ANTALL_SATS,
                                                                              MERGEABLE_TITTEL);

        return utbetalingBuilder.withUnderytelser(sammenlagteUnderytelser).withMelding(melding).createUtbetaling();
    }

    public static final class TransformComparator {

        public static final Comparator<UtbetalingTransformObjekt> DATO = new Comparator<UtbetalingTransformObjekt>() {
            @Override
            public int compare(UtbetalingTransformObjekt o1, UtbetalingTransformObjekt o2) {
                return -o1.getUtbetalingsdato().toLocalDate().compareTo(o2.getUtbetalingsdato().toLocalDate());
            }
        };



        public static final Comparator<UtbetalingTransformObjekt> TRANSFORMOBJEKT_ALLE_FELTER = new Comparator<UtbetalingTransformObjekt>() {
            @Override
            public int compare(UtbetalingTransformObjekt o1, UtbetalingTransformObjekt o2) {
                if(o1.equals(o2)) {
                    return 0;
                }
                return -1;
            }
        };

        public static final Comparator<Mergeable> MERGEABLE_ALLE_FELTER = new Comparator<Mergeable>() {
            @Override
            public int compare(Mergeable o1, Mergeable o2) {
                return TRANSFORMOBJEKT_ALLE_FELTER.compare((UtbetalingTransformObjekt)o1, (UtbetalingTransformObjekt)o2);
            }
        };

        public static final Comparator<Mergeable> MERGEABLE_DATO= new Comparator<Mergeable>() {
            @Override
            public int compare(Mergeable o1, Mergeable o2) {
                return DATO.compare((UtbetalingTransformObjekt)o1, (UtbetalingTransformObjekt)o2);
            }
        };

    }

    public int getAntall() {
        return antall;
    }
    public Double getBelop() {
        return belop;
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

    public String getMottakerKode() {
        return mottakerKode;
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

    public void setMelding(String melding) {
        this.melding = melding;
    }

    public DateTime getUtbetalingsdato() {
        return utbetalingsdato;
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
        private String mottakerKode;
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

        public UtbetalingTransformObjektBuilder withMottakerKode(String mottakerKode) {
            this.mottakerKode = mottakerKode;
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
            transformObjekt.mottakerKode = this.mottakerKode;
            transformObjekt.sats = this.sats;
            transformObjekt.status = this.status;
            transformObjekt.underYtelse = this.underYtelse;
            transformObjekt.utbetalingsdato = this.utbetalingsDato;
            transformObjekt.periode = this.periode;
            transformObjekt.valuta = this.valuta;
            transformObjekt.spesifikasjon = this.spesifikasjon;
            return transformObjekt;
        }

    }

    private static Utbetaling.UtbetalingBuilder lagUtbetalingBuilder(UtbetalingTransformObjekt objekt) {
        return Utbetaling.getBuilder()
                .withHovedytelse(objekt.getHovedYtelse())
                .withKontonr(objekt.getKontonummer())
                .withMelding(objekt.getMelding())
                .withStatus(objekt.getStatus())
                .withPeriode(objekt.getPeriode())
                .withValuta(objekt.getValuta())
                .withMottakernavn(objekt.getMottaker())
                .withMottakerId(objekt.getMottakerId())
                .withMottakerkode(objekt.getMottakerKode())
                .withUtbetalingsDato(objekt.getUtbetalingsdato());
    }

    private static void leggSammenBelop(Utbetaling.UtbetalingBuilder utbetalingBuilder, List<Underytelse> underytelser) {
        Double brutto = getBrutto(underytelser);
        Double trekk = getTrekk(underytelser);
        utbetalingBuilder.withBrutto(brutto);
        utbetalingBuilder.withTrekk(trekk);
        utbetalingBuilder.withUtbetalt(brutto + trekk);
    }
}
