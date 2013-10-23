package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.utils;

import java.util.Comparator;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Sak;
import org.apache.commons.collections15.Transformer;

public class Utils {

    public static final Transformer<Sak, String> ARKIVTEMA = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.getTemakode();
        }
    };

    public static final Comparator<Sak> SORTER_NYESTE_OVERST = new Comparator<Sak>() {
        @Override
        public int compare(Sak o1, Sak o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };
}
