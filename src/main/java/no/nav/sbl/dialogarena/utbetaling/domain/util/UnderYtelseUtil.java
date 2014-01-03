package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.SPESIFIKASJON;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.TREKK_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UTBETALT_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator.TITTEL;
import static org.apache.commons.lang3.StringUtils.join;

public class UnderYtelseUtil {

    public static Double getBrutto(List<Underytelse> underytelser) {
        return on(underytelser).map(UTBETALT_BELOP).reduce(sumDouble);
    }

    public static Double getTrekk(List<Underytelse> underytelser) {
        return on(underytelser).map(TREKK_BELOP).reduce(sumDouble);
    }

    public static List<Underytelse> leggSammenUnderYtelser(List<Underytelse> underytelser, Comparator<Underytelse> comparator) {
        LinkedList<Underytelse> ytelser = on(underytelser).collectIn(new LinkedList<Underytelse>());
        sort(ytelser, TITTEL);

        List<Underytelse> resultat = new ArrayList<>();
        while (ytelser.size() >= 1) {
            Underytelse ytelse1 = ytelser.get(0);

            List<Underytelse> skalMerges = new ArrayList<>();
            skalMerges.add(ytelse1);

            for (Underytelse ytelse2 : ytelser.subList(1, ytelser.size())) {
                if (comparator.compare(ytelse1,ytelse2) == 0) {
                    skalMerges.add(ytelse2);
                }
            }
            UnderytelseBuilder builder = mergeLikeUnderYtelser(skalMerges);

            resultat.add(builder.createUnderytelse());
            ytelser.removeAll(skalMerges);
        }

        return resultat;
    }

    private static UnderytelseBuilder mergeLikeUnderYtelser(List<Underytelse> ytelser) {
        Double belop = on(ytelser).map(BELOP).reduce(sumDouble);
        Set<String> spesifikasjoner = on(ytelser).map(SPESIFIKASJON).collectIn(new HashSet<String>());
        String spesifikasjon = join(spesifikasjoner, ". ");
        Underytelse ytelse = ytelser.get(0);
        return new UnderytelseBuilder().setSpesifikasjon(spesifikasjon)
                      .setBelop(belop)
                      .setTittel(ytelse.getTittel())
                      .setAntall(ytelse.getAntall())
                      .setSats(ytelse.getSats());
    }
}
