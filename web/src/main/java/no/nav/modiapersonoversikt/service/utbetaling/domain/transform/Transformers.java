package no.nav.modiapersonoversikt.service.utbetaling.domain.transform;

import no.nav.modiapersonoversikt.service.utbetaling.domain.*;
import no.nav.modiapersonoversikt.service.utbetaling.domain.Aktoer.AktoerType;
import no.nav.modiapersonoversikt.service.utbetaling.domain.util.YtelseUtils;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Transformerer WSUtbetaling til en eller flere hovedytelser, med utbetalingsinformasjon duplisert på hovedytelsen.
 */
public class Transformers {
    private static final Logger LOGGER = LoggerFactory.getLogger(Transformers.class);

    static final Function<WSSkatt, Double> SKATT_TRANSFORMER = WSSkatt::getSkattebeloep;

    private static final Function<WSTrekk, Trekk> TREKK_TRANSFORMER = wsTrekk -> new Trekk()
            .withTrekksType(wsTrekk.getTrekktype() != null ? wsTrekk.getTrekktype() : "")
            .withTrekkBeloep(wsTrekk.getTrekkbeloep())
            .withKreditor(wsTrekk.getKreditor());

    static final Function<WSYtelseskomponent, Underytelse> UNDERYTELSE_TRANSFORMER = wsYtelseskomponent -> new Underytelse()
            .withYtelsesType(wsYtelseskomponent.getYtelseskomponenttype())
            .withSatsBeloep(wsYtelseskomponent.getSatsbeloep())
            .withSatsType(wsYtelseskomponent.getSatstype())
            .withSatsAntall(wsYtelseskomponent.getSatsantall())
            .withYtelseBeloep(wsYtelseskomponent.getYtelseskomponentbeloep());


    public static final Function<WSUtbetaling, List<Hovedytelse>> TO_HOVEDYTELSE = wsUtbetaling -> wsUtbetaling.getYtelseListe()
            .stream()
            .map(wsYtelse -> new Hovedytelse()
                    .withMottakertype(YtelseUtils.mottakertypeForAktoer(wsUtbetaling.getUtbetaltTil()))
                    .withHovedytelsedato(determineHovedytelseDato(wsUtbetaling))
                    .withForfallsdato(wsUtbetaling.getForfallsdato())
                    .withUtbetalingsDato(wsUtbetaling.getUtbetalingsdato())
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
                    .withRefundertForOrg(createAktoer(wsYtelse.getRefundertForOrg()))
                    .withBruttoUtbetalt(wsYtelse.getYtelseskomponentersum())
                    .withSammenlagtTrekkBeloep()
                    .withErHovedutbetaling(wsUtbetaling.getYtelseListe().size() == 1)
            ).collect(toList());

    public static final Function<WSUtbetaling, Hovedutbetaling> SAMMENLAGT_UTBETALING_TRANSFORMER = wsUtbetaling -> new Hovedutbetaling()
            .withId(String.valueOf(createHovedutbetalingId(wsUtbetaling)))
            .withHovedytelsesdato(determineHovedytelseDato(wsUtbetaling))
            .withHovedytelser(TO_HOVEDYTELSE.apply(wsUtbetaling))
            .settUtbetaltSum()
            .withUtbetalingStatus(wsUtbetaling.getUtbetalingsstatus())
            .withIsUtbetalt(wsUtbetaling.getUtbetalingsdato() != null);


    /**
     * HovedytelseDato baserer seg på følgende prioritert rekkefølge:
     * 1. Hvis utbetalingsdato finnes så brukes denne
     * 2. Hvis forfallsdato finnes så brukes denne
     * 2. Hvis ikke brukes posteringsdatoen
     */
    static DateTime determineHovedytelseDato(WSUtbetaling wsUtbetaling) {
        if (wsUtbetaling.getUtbetalingsdato() != null) {
            return wsUtbetaling.getUtbetalingsdato();
        }
        if (wsUtbetaling.getForfallsdato() != null) {
            return wsUtbetaling.getForfallsdato();
        }
        return wsUtbetaling.getPosteringsdato();
    }

    static Double aggregateBruttoBeloep(Hovedytelse hovedytelse) {
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

    static List<Double> createSkatteListe(List<WSSkatt> skattListe) {
        if (skattListe == null) {
            return new ArrayList<>();
        }
        return skattListe
                .stream()
                .map(WSSkatt::getSkattebeloep)
                .collect(toList());
    }

    static List<Trekk> createTrekkliste(List<WSTrekk> trekkListe) {
        if (trekkListe == null) {
            return new ArrayList<>();
        }
        return trekkListe
                .stream()
                .map(TREKK_TRANSFORMER)
                .collect(toList());
    }

    static List<Underytelse> createUnderytelser(List<WSYtelseskomponent> ytelseskomponentListe) {
        if (ytelseskomponentListe == null || ytelseskomponentListe.isEmpty()) {
            return emptyList();
        }
        return ytelseskomponentListe
                .stream()
                .map(UNDERYTELSE_TRANSFORMER)
                .sorted((o1, o2) -> o2.getYtelseBeloep().compareTo(o1.getYtelseBeloep()))
                .collect(toList());
    }

    static Interval createPeriode(WSPeriode ytelsesperiode) {
        if (ytelsesperiode == null || (ytelsesperiode.getFom() == null || ytelsesperiode.getTom() == null)) {
            LOGGER.debug("Ytelsesperiode er tom, setter interval til new Interval(0,0)");
            return new Interval(0, 0);
        }
        return new Interval(ytelsesperiode.getFom(), ytelsesperiode.getTom());
    }

    static String determineKontoUtbetaltTil(WSUtbetaling wsUtbetaling) {
        WSBankkonto wsKonto = wsUtbetaling.getUtbetaltTilKonto();

        if (wsKonto == null || wsKonto.getKontonummer() == null || "".equals(wsKonto.getKontonummer())) {
            return wsUtbetaling.getUtbetalingsmetode();
        }
        return wsKonto.getKontonummer();
    }

    static Aktoer createAktoer(WSAktoer utbetaltTil) {
        if (utbetaltTil == null) {
            return null;
        }

        Aktoer aktoer = new Aktoer()
                .withAktoerId(utbetaltTil.getAktoerId())
                .withNavn(utbetaltTil.getNavn())
                .withAktoerType(getAktoerType(utbetaltTil));

        if (aktoer.getAktoerType().equals(AktoerType.PERSON)) {
            WSPerson wsPerson = (WSPerson) utbetaltTil;
            aktoer = aktoer.withDiskresjonskode(wsPerson.getDiskresjonskode());
        }

        return aktoer;
    }

    private static AktoerType getAktoerType(WSAktoer utbetaltTil) {
        if (utbetaltTil instanceof WSPerson) {
            return AktoerType.PERSON;
        } else if (utbetaltTil instanceof WSSamhandler) {
            return AktoerType.SAMHANDLER;
        } else if (utbetaltTil instanceof WSOrganisasjon) {
            return AktoerType.ORGANISASJON;
        } else {
            return AktoerType.PERSON;
        }
    }

    private static int createHovedytelseId(WSYtelse wsYtelse) {
        return (wsYtelse.getBilagsnummer() +
                wsYtelse.getSkattsum() +
                wsYtelse.getTrekksum() +
                wsYtelse.getYtelseNettobeloep())
                .hashCode();
    }

    private static int createHovedutbetalingId(WSUtbetaling utbetaling) {
        return createHovedytelseId(utbetaling.getYtelseListe().get(0));
    }
}
