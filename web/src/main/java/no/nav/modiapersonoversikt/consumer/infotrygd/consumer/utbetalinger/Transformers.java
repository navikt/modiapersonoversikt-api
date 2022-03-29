package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.utbetalinger;

import no.nav.modiapersonoversikt.commondomain.Periode;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.HistoriskUtbetaling;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.Kreditortrekk;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.utbetalinger.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSTrekk;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSYtelse;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Transformers {
    public static final List GYLDIGE_YTELSESTYPER  = Arrays.asList("SYKEPENGER", "FORELDREPENGER");

    public static final Function<WSUtbetaling, Hovedytelse> TO_HOVEDYTELSE = wsUtbetaling -> {
        Hovedytelse hovedytelse = new Hovedytelse();
        hovedytelse.setHistoriskUtbetalinger(mapHistoriskeUtbetalinger(wsUtbetaling));
        return hovedytelse;
    };

    private static List<HistoriskUtbetaling> mapHistoriskeUtbetalinger(final WSUtbetaling wsUtbetaling) {
        final List<WSYtelse> ytelseListe = wsUtbetaling.getYtelseListe();
        List<HistoriskUtbetaling> historiskeUtbetalinger = new ArrayList<>();
        if(ytelseListe == null || ytelseListe.isEmpty()) {
            return historiskeUtbetalinger;
        }

        for (WSYtelse ytelse: ytelseListe) {
            String ytelsestype = ytelse.getYtelsestype().getValue();
            if (GYLDIGE_YTELSESTYPER.contains(StringUtils.trim(StringUtils.upperCase(ytelsestype)))) {
                HistoriskUtbetaling historiskUtbetaling = new HistoriskUtbetaling()
                        .withYtelsesType(ytelsestype)
                        .withNettoBelop(ytelse.getYtelseNettobeloep())
                        .withSkattetrekk(ytelse.getSkattsum())
                        .withBruttoBelop(ytelse.getYtelseskomponentersum())
                        .withTrekk(getKreditorTrekkListe(ytelse.getTrekkListe()))
                        .withArbeidsgiverNavn(getArbeidsgiverNavn(ytelse))
                        .withArbeidsgiverOrgnr(getArbeidsgiverOrgNr(ytelse));

                if(wsUtbetaling.getUtbetalingsdato() != null) {
                    historiskUtbetaling.setUtbetalingsdato(wsUtbetaling.getUtbetalingsdato().toLocalDate());
                }

                historiskUtbetaling.setVedtak(createPeriode(ytelse.getYtelsesperiode()));
                historiskeUtbetalinger.add(historiskUtbetaling);
            }
        }
        return historiskeUtbetalinger;
    }

    private static List<Kreditortrekk> getKreditorTrekkListe(List<WSTrekk> trekkListe) {
        List<Kreditortrekk> kreditortrekkList = new ArrayList<>();

        for (WSTrekk trekk: trekkListe) {
            Kreditortrekk kreditortrekk = new Kreditortrekk(trekk.getKreditor(), trekk.getTrekkbeloep());
            kreditortrekkList.add(kreditortrekk);
        }

        return kreditortrekkList;
    }

    private static Periode createPeriode(WSPeriode ytelsesperiode) {
        if(ytelsesperiode != null) {
            DateTime fom = ytelsesperiode.getFom();
            DateTime tom = ytelsesperiode.getTom();
            return new Periode(fom.toLocalDate(),tom.toLocalDate());
        }
        return null;
    }

    private static String getArbeidsgiverOrgNr(WSYtelse ytelse) {
        if(ytelse.getRefundertForOrg() != null) {
            return ytelse.getRefundertForOrg().getAktoerId();
        } else {
            return "";
        }
    }

    private static String getArbeidsgiverNavn(WSYtelse ytelse) {
        if(ytelse.getRefundertForOrg() != null) {
            return ytelse.getRefundertForOrg().getNavn();
        } else {
            return "";
        }
    }
}
