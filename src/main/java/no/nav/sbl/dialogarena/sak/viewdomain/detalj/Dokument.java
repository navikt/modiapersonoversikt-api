package no.nav.sbl.dialogarena.sak.viewdomain.detalj;

import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import org.apache.commons.collections15.Predicate;

import static java.util.Arrays.asList;

public class Dokument {

    public static final Key<String> KODEVERK_REF = new Key<>("KODEVERK_REF");
    public static final Key<String> TILLEGGSTITTEL = new Key<>("TILLEGGSTITTEL");
    public static final Key<String> ARKIVREFERANSE = new Key<>("ARKIVREFERANSE");
    public static final Key<String> UUID = new Key<>("UUID");
    public static final Key<Innsendingsvalg> INNSENDINGSVALG = new Key<>("INNSENDINGSVALG");
    public static final Key<Boolean> HOVEDSKJEMA = new Key<>("HOVEDSKJEMA");


    public static final Predicate<Record<Dokument>> INNSENDT = dokument -> asList(Innsendingsvalg.INNSENDT, Innsendingsvalg.LASTET_OPP).contains(dokument.get(INNSENDINGSVALG));

    public static final Predicate<Record<Dokument>> ER_HOVEDSKJEMA = dokument -> dokument.get(HOVEDSKJEMA);

    public static final Predicate<Record<Dokument>> ER_KVITTERING = dokument -> dokument.get(KODEVERK_REF).equals("L7");

    public enum Innsendingsvalg {
        IKKE_VALGT, SEND_SENERE, LASTET_OPP, SENDES_IKKE, VEDLEGG_SENDES_IKKE, VEDLEGG_SENDES_AV_ANDRE, INNSENDT, VEDLEGG_ALLEREDE_SENDT
    }
}
