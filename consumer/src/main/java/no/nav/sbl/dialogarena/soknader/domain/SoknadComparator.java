package no.nav.sbl.dialogarena.soknader.domain;

import java.util.Comparator;

/**
 * Sorterer søknader basert på omvendt kronologisk dato slik at nyeste kommer først
 */
public class SoknadComparator implements Comparator<Soknad> {

    @Override
    public int compare(Soknad s1, Soknad s2) {
        return s2.getInnsendtDato().compareTo(s1.getInnsendtDato());
    }

}
