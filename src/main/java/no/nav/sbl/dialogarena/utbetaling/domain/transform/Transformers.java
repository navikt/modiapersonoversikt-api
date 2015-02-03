package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.*;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.apache.commons.collections15.Transformer;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Aktoer.aktoerId;
import static no.nav.sbl.dialogarena.utbetaling.domain.Aktoer.navn;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.Konto.kontonummer;
import static no.nav.sbl.dialogarena.utbetaling.domain.Konto.kontotype;
import static no.nav.sbl.dialogarena.utbetaling.domain.Trekk.kreditor;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.mottakertypeForAktoer;

/**
 * Transformerer WSUtbetaling til en eller flere hovedytelser, med utbetalingsinformasjon duplisert på hovedytelsen.
 */
public class Transformers {
    private static final Logger LOGGER = LoggerFactory.getLogger(Transformers.class);

    public static Comparator<Record<Underytelse>> UNDERYTELSE_DESC_BELOP = new Comparator<Record<Underytelse>>() {
        @Override
        public int compare(Record<Underytelse> o1, Record<Underytelse> o2) {
            return o2.get(Underytelse.ytelseBeloep).compareTo(o1.get(Underytelse.ytelseBeloep));
        }
    };

    public static Transformer<WSSkatt, Double> SKATT_TRANSFORMER = new Transformer<WSSkatt, Double>() {
        @Override
        public Double transform(WSSkatt wsSkatt) {
            return wsSkatt.getSkattebeloep();
        }
    };

    public static Transformer<WSTrekk, Record<Trekk>> TREKK_TRANSFORMER = new Transformer<WSTrekk, Record<Trekk>>() {
        @Override
        public Record<Trekk> transform(WSTrekk wsTrekk) {
            return new Record<Trekk>()
                    .with(Trekk.trekksType, wsTrekk.getTrekkstype().getValue())
                    .with(Trekk.trekkBeloep, wsTrekk.getTrekkbeloep())
                    .with(kreditor, wsTrekk.getKreditor());
        }
    };

    public static Transformer<WSYtelseskomponent, Record<Underytelse>> UNDERYTELSE_TRANSFORMER = new Transformer<WSYtelseskomponent, Record<Underytelse>>() {
        @Override
        public Record<Underytelse> transform(WSYtelseskomponent wsYtelseskomponent) {
            return new Record<Underytelse>()
                    .with(Underytelse.ytelsesType, wsYtelseskomponent.getYtelseskomponentstype() != null ? wsYtelseskomponent.getYtelseskomponentstype().getValue() : "")
                    .with(Underytelse.satsBeloep, wsYtelseskomponent.getSatsbeloep())
                    .with(Underytelse.satsType, wsYtelseskomponent.getSatstype() != null ? wsYtelseskomponent.getSatstype().getValue() : "")
                    .with(Underytelse.satsAntall, wsYtelseskomponent.getSatsantall())
                    .with(Underytelse.ytelseBeloep, wsYtelseskomponent.getYtelseskomponentBeloep());
        }
    };


    public static Transformer<WSUtbetaling, List<Record<Hovedytelse>>> toHovedytelse = new Transformer<WSUtbetaling, List<Record<Hovedytelse>>>() {
        @Override
        public List<Record<Hovedytelse>> transform(WSUtbetaling wsUtbetaling) {
            List<Record<Hovedytelse>> hovedytelser = new ArrayList<>();

            for(WSYtelse wsYtelse : wsUtbetaling.getYtelseListe()) {
                Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                    .with(mottakertype, mottakertypeForAktoer(wsUtbetaling.getUtbetaltTil()))
                    .with(posteringsdato, wsUtbetaling.getPosteringsdato())
                    .with(utbetaltTil, createAktoer(wsUtbetaling.getUtbetaltTil()))
                    .with(utbetalingsmelding, wsUtbetaling.getUtbetalingsmelding())
                    .with(utbetalingsDato, wsUtbetaling.getUtbetalingsdato())
                    .with(forfallsDato, wsUtbetaling.getForfallsdato())
                    .with(utbetaltTilKonto, createKonto(wsUtbetaling.getUtbetaltTilKonto()))
                    .with(utbetalingsmetode, wsUtbetaling.getUtbetalingsmetode() != null ? wsUtbetaling.getUtbetalingsmetode().getValue() : "")
                    .with(utbetalingsstatus, wsUtbetaling.getUtbetalingsstatus() != null ? wsUtbetaling.getUtbetalingsstatus().getValue() : "")
                    .with(id, String.valueOf(wsYtelse.hashCode()))
                    .with(ytelse, wsYtelse.getYtelsestype() != null ? wsYtelse.getYtelsestype().getValue() : "")
                    .with(ytelsesperiode, createPeriode(wsYtelse.getYtelsesperiode()))
                    .with(underytelseListe, createUnderytelser(wsYtelse.getYtelseskomponentListe()))
                    .with(sumUnderytelser, wsYtelse.getSumYtelseskomponenter())
                    .with(trekkListe, createTrekkliste(wsYtelse.getTrekkListe()))
                    .with(sumTrekk, wsYtelse.getSumTrekk())
                    .with(skattListe, createSkatteListe(wsYtelse.getSkattListe()))
                    .with(sumSkatt, wsYtelse.getSumSkatt())
                    .with(ytelseNettoBeloep, wsYtelse.getYtelseNettobeloep())
                    .with(bilagsnummer, wsYtelse.getBilagsnummer())
                    .with(rettighetshaver, createAktoer(wsYtelse.getRettighetshaver()))
                    .with(refundertForOrg, createAktoer(wsYtelse.getRefundertForOrg()));

                    hovedytelse = hovedytelse.with(ytelseBruttoBeloep, aggregateBruttoBeloep(hovedytelse));

                    hovedytelser.add(hovedytelse);
                }

            return hovedytelser;
        }
    };

    private static Double aggregateBruttoBeloep(Record<Hovedytelse> hovedytelse) {
        Double netto = hovedytelse.get(Hovedytelse.ytelseNettoBeloep);
        Double trekk = hovedytelse.get(Hovedytelse.sumTrekk);
        Double skatt = hovedytelse.get(Hovedytelse.sumSkatt);

        if(netto == null) {
            netto = 0.0;
        }

        if(trekk == null) {
            trekk = 0.0;
        }

        if(skatt == null) {
            skatt = 0.0;
        }

        return netto + trekk + skatt;
    }

    private static List<Double> createSkatteListe(List<WSSkatt> skattListe) {
        if(skattListe == null) {
            return new ArrayList<>();
        }
        return on(skattListe).map(SKATT_TRANSFORMER).collect();
    }

    private static List<Record<Trekk>> createTrekkliste(List<WSTrekk> trekkListe) {
        if(trekkListe == null) {
            return new ArrayList<>();
        }
        return on(trekkListe).map(TREKK_TRANSFORMER).collect();
    }

    private static List<Record<Underytelse>> createUnderytelser(List<WSYtelseskomponent> ytelseskomponentListe) {
        if(ytelseskomponentListe == null) {
            return new ArrayList<>();
        }
        return on(ytelseskomponentListe).map(UNDERYTELSE_TRANSFORMER).collect(reverseOrder(compareWith(Underytelse.ytelseBeloep)));
    }

    private static Interval createPeriode(WSPeriode ytelsesperiode) {
        if(ytelsesperiode == null) {
            LOGGER.debug("Ytelsesperiode er null, setter interval til new Interval(0,0)");
            return new Interval(0,0);
        }
        return new Interval(ytelsesperiode.getFom(), ytelsesperiode.getTom());
    }

    private static Record<Konto> createKonto(WSBankkonto utbetaltTilKonto) {
        if(utbetaltTilKonto == null) {
            return null;
        }
        return new Record<Konto>()
                .with(kontonummer, utbetaltTilKonto.getKontonummer())
                .with(kontotype, utbetaltTilKonto.getKontotype().getValue());
    }

    private static Record<Aktoer> createAktoer(WSAktoer utbetaltTil) {
        if(utbetaltTil == null) {
            return null;
        }
        return new Record<Aktoer>()
                .with(aktoerId, utbetaltTil.getAktoerId())
                .with(navn, utbetaltTil.getNavn());
    }
}
