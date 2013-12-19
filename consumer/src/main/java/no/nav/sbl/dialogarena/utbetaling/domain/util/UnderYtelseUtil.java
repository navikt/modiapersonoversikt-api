package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.TREKK_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UTBETALT_BELOP;
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
        List<Underytelse> resultat = new ArrayList<>();

        while (ytelser.size() > 1) {
            Underytelse ytelse1 = ytelser.get(0);
            Set<Underytelse> alleredeLagtTil = new HashSet<>();
            for (Underytelse ytelse2 : ytelser.subList(1, ytelser.size())) {
                if (ytelse1.equals(ytelse2)) {
                    Underytelse underytelse = mergeLikeUnderYtelser(ytelse1.getTittel(), ytelse1, ytelse2);
                    resultat.add(underytelse);
                } else {
                    resultat.add(ytelse2);
                }
                alleredeLagtTil.add(ytelse2);
            }
            ytelser.removeAll(alleredeLagtTil);
        }
        return resultat;
    }


    private static Underytelse mergeLikeUnderYtelser(String tittel, Underytelse a, Underytelse b) {
        Double belop = a.getBelop() + b.getBelop();
        Set<String> spesifikasjoner = new HashSet<>();
        spesifikasjoner.addAll(Arrays.asList(a.getSpesifikasjon(), b.getSpesifikasjon()));
        String spesifikasjon = join(spesifikasjoner, ". ");
        return new Underytelse(tittel, spesifikasjon, a.getAntall(), belop, a.getSats());
    }

}
