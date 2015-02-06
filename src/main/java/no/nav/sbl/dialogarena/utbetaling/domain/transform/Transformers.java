package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.*;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    public static final Transformer<WSSkatt, Double> SKATT_TRANSFORMER = new Transformer<WSSkatt, Double>() {
        @Override
        public Double transform(WSSkatt wsSkatt) {
            return wsSkatt.getSkattebeloep();
        }
    };

    public static final Transformer<WSTrekk, Record<Trekk>> TREKK_TRANSFORMER = new Transformer<WSTrekk, Record<Trekk>>() {
        @Override
        public Record<Trekk> transform(WSTrekk wsTrekk) {
            return new Record<Trekk>()
                    .with(Trekk.trekksType, wsTrekk.getTrekktype() != null ? wsTrekk.getTrekktype().getValue() : "")
                    .with(Trekk.trekkBeloep, wsTrekk.getTrekkbeloep())
                    .with(kreditor, wsTrekk.getKreditor());
        }
    };

    public static final Transformer<WSYtelseskomponent, Record<Underytelse>> UNDERYTELSE_TRANSFORMER = new Transformer<WSYtelseskomponent, Record<Underytelse>>() {
        @Override
        public Record<Underytelse> transform(WSYtelseskomponent wsYtelseskomponent) {
            return new Record<Underytelse>()
                    .with(Underytelse.ytelsesType, wsYtelseskomponent.getYtelseskomponenttype() != null ? wsYtelseskomponent.getYtelseskomponenttype().getValue() : "")
                    .with(Underytelse.satsBeloep, wsYtelseskomponent.getSatsbeloep())
                    .with(Underytelse.satsType, wsYtelseskomponent.getSatstype() != null ? wsYtelseskomponent.getSatstype().getValue() : "")
                    .with(Underytelse.satsAntall, wsYtelseskomponent.getSatsantall())
                    .with(Underytelse.ytelseBeloep, wsYtelseskomponent.getYtelseskomponentbeloep());
        }
    };


    public static final Transformer<WSUtbetaling, List<Record<Hovedytelse>>> TO_HOVEDYTELSE = new Transformer<WSUtbetaling, List<Record<Hovedytelse>>>() {
        @Override
        public List<Record<Hovedytelse>> transform(WSUtbetaling wsUtbetaling) {
            List<Record<Hovedytelse>> hovedytelser = new ArrayList<>();

            for(WSYtelse wsYtelse : wsUtbetaling.getYtelseListe()) {
                Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                    .with(mottakertype, mottakertypeForAktoer(wsUtbetaling.getUtbetaltTil()))
                    .with(hovedytelsedato, determineHovedytelseDato(wsUtbetaling))
                    .with(utbetaltTil, createAktoer(wsUtbetaling.getUtbetaltTil()))
                    .with(utbetalingsmelding, wsUtbetaling.getUtbetalingsmelding())
                    .with(forfallsDato, wsUtbetaling.getForfallsdato())
                    .with(utbetaltTilKonto, createKonto(wsUtbetaling.getUtbetaltTilKonto()))
                    .with(utbetalingsmetode, wsUtbetaling.getUtbetalingsmetode() != null ? wsUtbetaling.getUtbetalingsmetode().getValue() : "")
                    .with(utbetalingsstatus, wsUtbetaling.getUtbetalingsstatus() != null ? wsUtbetaling.getUtbetalingsstatus().getValue() : "")
                    .with(id, String.valueOf(wsYtelse.hashCode()))
                    .with(ytelse, wsYtelse.getYtelsestype() != null ? wsYtelse.getYtelsestype().getValue() : "")
                    .with(ytelsesperiode, createPeriode(wsYtelse.getYtelsesperiode()))
                    .with(underytelseListe, createUnderytelser(wsYtelse.getYtelseskomponentListe()))
                    .with(trekkListe, createTrekkliste(wsYtelse.getTrekkListe()))
                    .with(sumTrekk, wsYtelse.getTrekksum())
                    .with(skattListe, createSkatteListe(wsYtelse.getSkattListe()))
                    .with(sumSkatt, wsYtelse.getSkattsum())
                    .with(nettoUtbetalt, wsYtelse.getYtelseNettobeloep())
                    .with(bilagsnummer, wsYtelse.getBilagsnummer())
                    .with(rettighetshaver, createAktoer(wsYtelse.getRettighetshaver()))
                    .with(refundertForOrg, createAktoer(wsYtelse.getRefundertForOrg()));

                    hovedytelse = hovedytelse.with(bruttoUtbetalt, wsYtelse.getYtelseskomponentersum());
                    hovedytelse = hovedytelse.with(sammenlagtTrekkBeloep, aggregateTrekkBeloep(hovedytelse));

                    hovedytelser.add(hovedytelse);
                }


            return hovedytelser;
        }
    };

    /**
     * Hvis utbetalingsdato finnes, returneres denne
     * Ellers returneres posteringsdato
     * @param wsUtbetaling
     * @return
     */
    private static DateTime determineHovedytelseDato(WSUtbetaling wsUtbetaling) {
        if(wsUtbetaling.getUtbetalingsdato() != null) {
            return wsUtbetaling.getUtbetalingsdato();
        }
        return wsUtbetaling.getPosteringsdato();
    }

    protected static Double aggregateTrekkBeloep(Record<Hovedytelse> hovedytelse) {
        Double trekk = hovedytelse.get(Hovedytelse.sumTrekk);
        Double skatt = hovedytelse.get(Hovedytelse.sumSkatt);

        if(trekk == null) {
            trekk = 0.0;
        }

        if(skatt == null) {
            skatt = 0.0;
        }

        return trekk + skatt;
    }

    protected static Double aggregateBruttoBeloep(Record<Hovedytelse> hovedytelse) {
        Double netto = hovedytelse.get(Hovedytelse.nettoUtbetalt);
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

        return netto + (trekk) + (skatt);
    }

    protected static List<Double> createSkatteListe(List<WSSkatt> skattListe) {
        if(skattListe == null) {
            return new ArrayList<>();
        }
        return on(skattListe).map(SKATT_TRANSFORMER).collect();
    }

    protected static List<Record<Trekk>> createTrekkliste(List<WSTrekk> trekkListe) {
        if(trekkListe == null) {
            return new ArrayList<>();
        }
        return on(trekkListe).map(TREKK_TRANSFORMER).collect();
    }

    protected static List<Record<Underytelse>> createUnderytelser(List<WSYtelseskomponent> ytelseskomponentListe) {
        if(ytelseskomponentListe == null) {
            return new ArrayList<>();
        }
        return on(ytelseskomponentListe).map(UNDERYTELSE_TRANSFORMER).collect(reverseOrder(compareWith(Underytelse.ytelseBeloep)));
    }

    protected static Interval createPeriode(WSPeriode ytelsesperiode) {
        if(ytelsesperiode == null) {
            LOGGER.debug("Ytelsesperiode er null, setter interval til new Interval(0,0)");
            return new Interval(0,0);
        }
        return new Interval(ytelsesperiode.getFom(), ytelsesperiode.getTom());
    }

    protected static Record<Konto> createKonto(WSBankkonto utbetaltTilKonto) {
        if(utbetaltTilKonto == null) {
            return null;
        }
        return new Record<Konto>()
                .with(kontonummer, utbetaltTilKonto.getKontonummer())
                .with(kontotype, utbetaltTilKonto.getKontotype().getValue());
    }

    protected static Record<Aktoer> createAktoer(WSAktoer utbetaltTil) {
        if(utbetaltTil == null) {
            return null;
        }
        return new Record<Aktoer>()
                .with(aktoerId, utbetaltTil.getAktoerId())
                .with(navn, utbetaltTil.getNavn());
    }
}
