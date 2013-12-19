package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
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

    public static List<Underytelse> leggSammenUnderYtelser(List<Underytelse> underytelser) {
        ArrayList<Underytelse> ytelser = on(underytelser).collectIn(new ArrayList<Underytelse>());
        Collections.sort(ytelser, TITTEL);

        List<Underytelse> resultat = new ArrayList<>();

        while (ytelser.size() > 1) {
            UnderytelseBuilder builder = new UnderytelseBuilder();
            Underytelse ytelse1 = ytelser.get(0);
            builder = builder.setTittel(ytelse1.getTittel())
                             .setAntall(ytelse1.getAntall())
                             .setSats(ytelse1.getSats())
                             .setSpesifikasjon(ytelse1.getSpesifikasjon())
                             .setBelop(ytelse1.getBelop());

            Set<Underytelse> alleredeLagtTil = new HashSet<>();
            for (Underytelse ytelse2 : ytelser.subList(1, ytelser.size())) {
                if (ytelse1.equals(ytelse2)) {
                    builder = mergeLikeUnderYtelser(ytelse1, ytelse2, builder);
                    alleredeLagtTil.add(ytelse2);
                }
            }
            alleredeLagtTil.add(ytelse1);
            ytelser.removeAll(alleredeLagtTil);

            resultat.add(builder.createUnderytelse());
        }

        return resultat;
    }

    private static UnderytelseBuilder mergeLikeUnderYtelser(Underytelse a, Underytelse b, UnderytelseBuilder builder) {
        Double belop = a.getBelop() + b.getBelop();
        Set<String> spesifikasjoner = new HashSet<>();
        spesifikasjoner.addAll(Arrays.asList(a.getSpesifikasjon(), b.getSpesifikasjon()));
        String spesifikasjon = join(spesifikasjoner, ". ");
        return builder.setSpesifikasjon(spesifikasjon).setAntall(a.getAntall()).setBelop(belop).setSats(a.getSats());
    }

}
