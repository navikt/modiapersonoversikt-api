package no.nav.sbl.dialogarena.utbetaling.domain.transform;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator.MERGEABLE_ALLE_FELTER;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator.MERGEABLE_DATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.getBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.MergeUtil.merge;
import static org.apache.commons.lang3.StringUtils.join;
import static org.slf4j.LoggerFactory.getLogger;

public final class UtbetalingTransformer {

    private static Logger logger = getLogger(UtbetalingTransformer.class);


    private static final Integer DEFAULT_INT_VALUE = 1337;
    private static final Double DEFAULT_DOUBLE_VALUE = 13.37;
    private static final String DEFAULT_STRING_VALUE = "TRETTENTRETTISJU";
    private static final DateTime DEFAULT_DATETIME_VALUE = new DateTime().minusDays(1337);
    private static final Interval DEFAULT_INTERVAL_VALUE = new Interval(DEFAULT_DATETIME_VALUE, new DateTime());

    /**
     * Transformasjonen gjøres i tre steg:
     * - Trekk ut skatt
     * - Transformer til transformobjekter
     * - Slå sammen til Utbetalinger
     */
    public static List<Utbetaling> createUtbetalinger(List<WSUtbetaling> wsUtbetalinger, String fnr) {
        transformerSkatt(wsUtbetalinger);
        return transformerTilUtbetalinger(createTransformObjekter(wsUtbetalinger, fnr), MERGEABLE_ALLE_FELTER);
    }

    /**
     * Gjør om en liste av WSUtbetalinger til en liste av UtbetalingTransformObjekt.
     * Et UtbetalingTransformObjekt tilsvarer en WSPosteringsDetalj i utbetalingene, lagt til informasjon fra WSUtbetalingen og WSBilaget
     * detaljen kom fra.
     */
    static List<UtbetalingTransformObjekt> createTransformObjekter(List<WSUtbetaling> wsUtbetalinger, String fnr) {
        logger.info("---- Transformasjon av utbetalinger ----");
        List<UtbetalingTransformObjekt> list = new ArrayList<>();
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            WSMottaker wsMottaker = wsUtbetaling.getUtbetalingMottaker();
            String mottakerId = wsMottaker != null ? wsMottaker.getMottakerId() : "";
            String mottaker = wsMottaker != null ? wsMottaker.getNavn() : "";
            String mottakerKode = transformMottakerKode(wsMottaker, fnr);

            Interval periode = wsUtbetaling.getUtbetalingsPeriode() != null ?
                    new Interval(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato(), wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato())
                    : new Interval(0, 0);
            String delimiter = ", ";

            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                String melding = transformMelding(wsBilag, delimiter);

                logger.info("---- Transformasjon fra WSPosteringsdetaljer => transformObjekt ----");
                for (WSPosteringsdetaljer detalj : wsBilag.getPosteringsdetaljerListe()) {
                    UtbetalingTransformObjekt transformObjekt = getBuilder()
                            .withAntall(optional(detalj.getAntall()).getOrElse(DEFAULT_INT_VALUE))
                            .withBelop(optional(detalj.getBelop()).getOrElse(DEFAULT_DOUBLE_VALUE))
                                    .withHovedYtelse(optional(detalj.getKontoBeskrHoved()).getOrElse(DEFAULT_STRING_VALUE))
                                            .withKontonummer(optional(wsUtbetaling.getGironr()).getOrElse(DEFAULT_STRING_VALUE))
                                            .withMottaker(optional(mottaker).getOrElse(DEFAULT_STRING_VALUE))
                                            .withMottakerId(optional(mottakerId).getOrElse(DEFAULT_STRING_VALUE))
                                            .withMottakerKode(optional(mottakerKode).getOrElse(DEFAULT_STRING_VALUE))
                                            .withSats(optional(detalj.getSats()).getOrElse(DEFAULT_DOUBLE_VALUE))
                                            .withStatus(optional(wsUtbetaling.getStatusBeskrivelse()).getOrElse(DEFAULT_STRING_VALUE))
                                            .withSpesifikasjon(optional(detalj.getSpesifikasjon()).getOrElse(DEFAULT_STRING_VALUE))
                                            .withUnderYtelse(transformerUnderbeskrivelse(detalj.getKontoBeskrUnder(), detalj.getKontoBeskrHoved()))
                                            .withUtbetalingsDato(optional(wsUtbetaling.getUtbetalingDato()).getOrElse(DEFAULT_DATETIME_VALUE))
                                            .withPeriode(optional(periode).getOrElse(DEFAULT_INTERVAL_VALUE))
                                            .withValuta(optional(wsUtbetaling.getValuta()).getOrElse(DEFAULT_STRING_VALUE))
                                            .build();
                    transformObjekt.setMelding(melding);
                    list.add(transformObjekt);
                }
            }
        }
        return list;
    }

    /**
     * Tar inn en liste av transformobjekter. Slår dem sammen og lager en liste av Utbetalinger.
     *
     * Forutsetninger for at transformasjonen skal bli riktig:
     * <p>- Alle utbetalinger har en utbetalingsdato</p>
     * <p>- Alle posteringsdetaljer:</p>
     *    <p> - Har en kontoBeskrHoved som tilsvarer hovedytelsen</p>
     *    <p> - i et bilag har samme hovedytelse (utenom Skatt)</p>
     *    <p> - har et beløp.</p>
     */
    private static List<Utbetaling> transformerTilUtbetalinger(List<UtbetalingTransformObjekt> transformObjekter, Comparator<Mergeable<Utbetaling>> comparator) {
        return merge(new ArrayList<Mergeable<Utbetaling>>(transformObjekter), comparator, MERGEABLE_DATO);
    }

    static String transformMottakerKode(WSMottaker wsMottaker, String fnr) {
        if (wsMottaker != null && !fnr.equals(wsMottaker.getMottakerId())) {
            return ARBEIDSGIVER;
        }
        return BRUKER;
    }

    static void transformerSkatt(List<WSUtbetaling> wsUtbetalinger) {
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                trekkUtSkatteOpplysninger(wsBilag.getPosteringsdetaljerListe());
            }
        }
    }

    /**
     * Kobler skattetrekket til ytelsen i bilaget.
     * Forutsetter at alle posteringsdetaljer i et bilag har samme hovedytelse.
     *
     * En WSPosteringDetalj inneholder skatteopplysning hvis kontoBeskrHoved er "Skatt". Den har også en
     * underbeskrivelse, f.eks "Forskuddstrekk skatt". Endre kontoBeskrHoved til det samme som de andre
     * posteringsdetaljene i samme WSBilag. (Alle posteringdetaljer i samme bilag har samme kontoBeskrHoved).
     */
    private static void trekkUtSkatteOpplysninger(List<WSPosteringsdetaljer> detaljer) {
        List<WSPosteringsdetaljer> skatteDetaljer = new ArrayList<>();
        for (WSPosteringsdetaljer detalj : detaljer) {
            if ("SKATT".equalsIgnoreCase(detalj.getKontoBeskrHoved())) {
                skatteDetaljer.add(detalj);
            }
        }
        if (skatteDetaljer.isEmpty() || detaljer.isEmpty()) {
            return;
        }
        String beskrivelse = detaljer.get(0).getKontoBeskrHoved();
        for (WSPosteringsdetaljer skatt : skatteDetaljer) {
            skatt.setKontoBeskrHoved(beskrivelse);
        }
    }

    /**
     * Hvis det er en underytelsebeskrivelse(kontoBeskrUnder), bruk den, ellers bruk beskrivelsen av hovedytelsen (kontoBeskrHoved).
     */
    private static String transformerUnderbeskrivelse(String kontoBeskrUnder, String kontoBeskrHoved) {
        return (kontoBeskrUnder != null && !kontoBeskrUnder.isEmpty() ? kontoBeskrUnder : kontoBeskrHoved);
    }

    private static String transformMelding(WSBilag wsBilag, String delimiter) {
        List<WSMelding> meldingListe = wsBilag.getMeldingListe();
        List<String> strings = new ArrayList<>();
        for (WSMelding wsMelding : meldingListe) {
            strings.add(wsMelding.getMeldingtekst());
        }
        return join(strings, delimiter);
    }
}
