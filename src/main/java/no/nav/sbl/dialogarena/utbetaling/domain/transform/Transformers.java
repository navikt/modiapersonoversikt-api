package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer.AktoerType;
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
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
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


    public static final Transformer<WSUtbetaling, List<Hovedytelse>> TO_HOVEDYTELSE = wsUtbetaling -> {
        List<Hovedytelse> hovedytelser = new ArrayList<>();

        for (WSYtelse wsYtelse : wsUtbetaling.getYtelseListe()) {
            Hovedytelse hovedytelse = new Hovedytelse()
                    .withMottakertype(mottakertypeForAktoer(wsUtbetaling.getUtbetaltTil()))
                    .withHovedytelsedato(determineHovedytelseDato(wsUtbetaling))
                    .withForfallsdato(wsUtbetaling.getForfallsdato())
                    .withUtbetalingsDato(optional(wsUtbetaling.getUtbetalingsdato()))
                    .withPosteringsDato(wsUtbetaling.getPosteringsdato())
                    .withUtbetaltTil(createAktoer(wsUtbetaling.getUtbetaltTil()))
                    .withUtbetalingsmelding(wsUtbetaling.getUtbetalingsmelding())
                    .withUtbetaltTilKonto(determineKontoUtbetaltTil(wsUtbetaling))
                    .withUtbetalingsmetode(wsUtbetaling.getUtbetalingsmetode())
                    .withUtbetalingsstatus(wsUtbetaling.getUtbetalingsstatus())
                    .withId(String.valueOf(createHovedytelseId(wsYtelse)))
                    .withYtelse(wsYtelse.getYtelsestype() != null ? wsYtelse.getYtelsestype().getValue().toLowerCase() : "")
                    .withYtelsesperiode(createPeriode(wsYtelse.getYtelsesperiode()))
                    .withUnderytelseListe(createUnderytelser(wsYtelse.getYtelseskomponentListe()))
                    .withTrekkListe(createTrekkliste(wsYtelse.getTrekkListe()))
                    .withSumTrekk(wsYtelse.getTrekksum())
                    .withSkattListe(createSkatteListe(wsYtelse.getSkattListe()))
                    .withSumSkatt(wsYtelse.getSkattsum())
                    .withNettoUtbetalt(wsYtelse.getYtelseNettobeloep())
                    .withBilagsnummer(wsYtelse.getBilagsnummer())
                    .withRettighetshaver(createAktoer(wsYtelse.getRettighetshaver()))
                    .withRefundertForOrg(createAktoer(wsYtelse.getRefundertForOrg()));

            hovedytelse = hovedytelse.withBruttoUtbetalt(wsYtelse.getYtelseskomponentersum());
            hovedytelse = hovedytelse.withSammenlagtTrekkBeloep(aggregateTrekkBeloep(hovedytelse));

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

    protected static Double aggregateTrekkBeloep(Hovedytelse hovedytelse) {
        Double trekk = hovedytelse.getSumTrekk();
        Double skatt = hovedytelse.getSumSkatt();

        if (trekk == null) {
            trekk = 0.0;
        }

        if (skatt == null) {
            skatt = 0.0;
        }

        return trekk + skatt;
    }

    protected static Double aggregateBruttoBeloep(Hovedytelse hovedytelse) {
        Double netto = hovedytelse.getNettoUtbetalt();
        Double trekk = hovedytelse.getSumTrekk();
        Double skatt = hovedytelse.getSumSkatt();


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
        return skattListe
                .stream()
                .map(wsSkatt -> wsSkatt.getSkattebeloep())
                .collect(Collectors.toList());
    }

    protected static List<Record<Trekk>> createTrekkliste(List<WSTrekk> trekkListe) {
        if (trekkListe == null) {
            return new ArrayList<>();
        }
        return trekkListe
                .stream()
                .map(wsTrekk -> new Record<Trekk>()
                        .with(Trekk.trekksType, wsTrekk.getTrekktype() != null ? wsTrekk.getTrekktype() : "")
                        .with(Trekk.trekkBeloep, wsTrekk.getTrekkbeloep())
                        .with(kreditor, wsTrekk.getKreditor()))
                .collect(Collectors.toList());
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
