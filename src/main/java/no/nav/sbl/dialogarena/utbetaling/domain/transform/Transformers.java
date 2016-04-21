package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer.*;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Trekk;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.utbetaling.domain.Aktoer.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.Trekk.kreditor;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.mottakertypeForAktoer;

/**
 * Transformerer WSUtbetaling til en eller flere hovedytelser, med utbetalingsinformasjon duplisert på hovedytelsen.
 */
public class Transformers {
    private static final Logger LOGGER = LoggerFactory.getLogger(Transformers.class);

    public static final Transformer<WSSkatt, Double> SKATT_TRANSFORMER = wsSkatt -> wsSkatt.getSkattebeloep();

    public static final Transformer<WSTrekk, Record<Trekk>> TREKK_TRANSFORMER = wsTrekk -> new Record<Trekk>()
            .with(Trekk.trekksType, wsTrekk.getTrekktype() != null ? wsTrekk.getTrekktype() : "")
            .with(Trekk.trekkBeloep, wsTrekk.getTrekkbeloep())
            .with(kreditor, wsTrekk.getKreditor());

    public static final Transformer<WSYtelseskomponent, Record<Underytelse>> UNDERYTELSE_TRANSFORMER = wsYtelseskomponent -> new Record<Underytelse>()
            .with(Underytelse.ytelsesType, wsYtelseskomponent.getYtelseskomponenttype())
            .with(Underytelse.satsBeloep, wsYtelseskomponent.getSatsbeloep())
            .with(Underytelse.satsType, wsYtelseskomponent.getSatstype())
            .with(Underytelse.satsAntall, wsYtelseskomponent.getSatsantall())
            .with(Underytelse.ytelseBeloep, wsYtelseskomponent.getYtelseskomponentbeloep());


    public static final Transformer<WSUtbetaling, List<Record<Hovedytelse>>> TO_HOVEDYTELSE = wsUtbetaling -> {
        List<Record<Hovedytelse>> hovedytelser = new ArrayList<>();

        for (WSYtelse wsYtelse : wsUtbetaling.getYtelseListe()) {
            Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                    .with(mottakertype, mottakertypeForAktoer(wsUtbetaling.getUtbetaltTil()))
                    .with(hovedytelsedato, determineHovedytelseDato(wsUtbetaling))
                    .with(forfallsdato, wsUtbetaling.getForfallsdato())
                    .with(utbetalingsDato, optional(wsUtbetaling.getUtbetalingsdato()))
                    .with(posteringsDato, wsUtbetaling.getPosteringsdato())
                    .with(utbetaltTil, createAktoer(wsUtbetaling.getUtbetaltTil()))
                    .with(utbetalingsmelding, wsUtbetaling.getUtbetalingsmelding())
                    .with(utbetaltTilKonto, determineKontoUtbetaltTil(wsUtbetaling))
                    .with(utbetalingsmetode, wsUtbetaling.getUtbetalingsmetode())
                    .with(utbetalingsstatus, wsUtbetaling.getUtbetalingsstatus())
                    .with(id, String.valueOf(createHovedytelseId(wsYtelse)))
                    .with(ytelse, wsYtelse.getYtelsestype() != null ? wsYtelse.getYtelsestype().getValue().toLowerCase() : "")
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
    };

    /**
     * HovedytelseDato baserer seg på følgende prioritert rekkefølge:
     * 1. Hvis utbetalingsdato finnes så brukes denne
     * 2. Hvis ikke brukes posteringsdatoen (som vises sammen med forfallsdato for å indikere når utbetalingen vil skje)
     *
     * @param wsUtbetaling
     * @return
     */
    protected static DateTime determineHovedytelseDato(WSUtbetaling wsUtbetaling) {
        if (wsUtbetaling.getUtbetalingsdato() != null) {
            return wsUtbetaling.getUtbetalingsdato();
        }

        return wsUtbetaling.getPosteringsdato();
    }

    protected static Double aggregateTrekkBeloep(Record<Hovedytelse> hovedytelse) {
        Double trekk = hovedytelse.get(Hovedytelse.sumTrekk);
        Double skatt = hovedytelse.get(Hovedytelse.sumSkatt);

        if (trekk == null) {
            trekk = 0.0;
        }

        if (skatt == null) {
            skatt = 0.0;
        }

        return trekk + skatt;
    }

    protected static Double aggregateBruttoBeloep(Record<Hovedytelse> hovedytelse) {
        Double netto = hovedytelse.get(Hovedytelse.nettoUtbetalt);
        Double trekk = hovedytelse.get(Hovedytelse.sumTrekk);
        Double skatt = hovedytelse.get(Hovedytelse.sumSkatt);


        if (netto == null) {
            netto = 0.0;
        }

        if (trekk == null) {
            trekk = 0.0;
        }

        if (skatt == null) {
            skatt = 0.0;
        }

        return netto + (trekk) + (skatt);
    }

    protected static List<Double> createSkatteListe(List<WSSkatt> skattListe) {
        if (skattListe == null) {
            return new ArrayList<>();
        }
        return on(skattListe).map(SKATT_TRANSFORMER).collect();
    }

    protected static List<Record<Trekk>> createTrekkliste(List<WSTrekk> trekkListe) {
        if (trekkListe == null) {
            return new ArrayList<>();
        }
        return on(trekkListe).map(TREKK_TRANSFORMER).collect();
    }

    protected static Optional<List<Record<Underytelse>>> createUnderytelser(List<WSYtelseskomponent> ytelseskomponentListe) {
        if (ytelseskomponentListe == null || ytelseskomponentListe.isEmpty()) {
            return Optional.none();
        }
        return optional(on(ytelseskomponentListe).map(UNDERYTELSE_TRANSFORMER).collect(reverseOrder(compareWith(Underytelse.ytelseBeloep))));
    }

    protected static Interval createPeriode(WSPeriode ytelsesperiode) {
        if (ytelsesperiode == null || (ytelsesperiode.getFom() == null || ytelsesperiode.getTom() == null)) {
            LOGGER.debug("Ytelsesperiode er tom, setter interval til new Interval(0,0)");
            return new Interval(0, 0);
        }
        return new Interval(ytelsesperiode.getFom(), ytelsesperiode.getTom());
    }

    protected static String determineKontoUtbetaltTil(WSUtbetaling wsUtbetaling) {
        WSBankkonto wsKonto = wsUtbetaling.getUtbetaltTilKonto();

        if (wsKonto == null || StringUtils.isEmpty(wsKonto.getKontonummer())) {
            return wsUtbetaling.getUtbetalingsmetode();
        }
        return wsKonto.getKontonummer();
    }

    protected static Record<Aktoer> createAktoer(WSAktoer utbetaltTil) {
        if (utbetaltTil == null) {
            return null;
        }

        Record<Aktoer> aktoer = new Record<Aktoer>()
                .with(aktoerId, utbetaltTil.getAktoerId())
                .with(navn, utbetaltTil.getNavn())
                .with(aktoerType, getAktoerType(utbetaltTil));

        if(aktoer.get(Aktoer.aktoerType).equals(AktoerType.PERSON)) {
            WSPerson wsPerson = (WSPerson) utbetaltTil;
            aktoer = aktoer.with(Aktoer.diskresjonskode, wsPerson.getDiskresjonskode());
        }

        return aktoer;
    }

    protected static AktoerType getAktoerType(WSAktoer utbetaltTil) {
        if(utbetaltTil instanceof WSPerson) {
            return AktoerType.PERSON;
        } else if(utbetaltTil instanceof WSSamhandler) {
            return AktoerType.SAMHANDLER;
        } else if(utbetaltTil instanceof WSOrganisasjon) {
            return AktoerType.ORGANISASJON;
        } else {
            return AktoerType.PERSON;
        }
    }

    protected static int createHovedytelseId(WSYtelse wsYtelse) {
        return new StringBuilder()
                .append(wsYtelse.getBilagsnummer())
                .append(wsYtelse.getSkattsum())
                .append(wsYtelse.getTrekksum())
                .append(wsYtelse.getYtelseNettobeloep())
                .toString()
                .hashCode();
    }
}
