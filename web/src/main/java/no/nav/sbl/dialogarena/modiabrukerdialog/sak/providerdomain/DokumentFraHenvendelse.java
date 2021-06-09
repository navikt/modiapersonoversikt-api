package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;

import java.util.function.Predicate;

import static java.util.Arrays.asList;

public class DokumentFraHenvendelse {

    private String kodeverkRef;
    private String tilleggstittel;
    private String arkivreferanse;
    private String uuid;
    private Innsendingsvalg innsendingsvalg;
    private Boolean hovedskjema;

    public static final Predicate<DokumentFraHenvendelse> INNSENDT = dokument -> asList(Innsendingsvalg.INNSENDT, Innsendingsvalg.LASTET_OPP).contains(dokument.innsendingsvalg);

    public static final Predicate<DokumentFraHenvendelse> ER_KVITTERING = dokument -> dokument.kodeverkRef.equals("L7");

    public enum Innsendingsvalg {
        IKKE_VALGT, SEND_SENERE, LASTET_OPP, SENDES_IKKE, VEDLEGG_SENDES_IKKE, VEDLEGG_SENDES_AV_ANDRE, INNSENDT, VEDLEGG_ALLEREDE_SENDT
    }

    public String getTilleggstittel() {
        return tilleggstittel;
    }

    public String getArkivreferanse() {
        return arkivreferanse;
    }

    public String getKodeverkRef() {
        return kodeverkRef;
    }

    public String getUuid() {
        return uuid;
    }

    public Boolean erHovedskjema() {
        return hovedskjema;
    }

    public Innsendingsvalg getInnsendingsvalg() {
        return innsendingsvalg;
    }


    public DokumentFraHenvendelse withKodeverkRef(String kodeverkRef) {
        this.kodeverkRef = kodeverkRef;
        return this;
    }

    public DokumentFraHenvendelse withTilleggstittel(String tilleggstittel) {
        this.tilleggstittel = tilleggstittel;
        return this;
    }

    public DokumentFraHenvendelse withArkivreferanse(String arkivreferanse) {
        this.arkivreferanse = arkivreferanse;
        return this;
    }

    public DokumentFraHenvendelse withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public DokumentFraHenvendelse withErHovedskjema(Boolean hovedskjema) {
        this.hovedskjema = hovedskjema;
        return this;
    }

    public DokumentFraHenvendelse withInnsendingsvalg(Innsendingsvalg innsendingsvalg) {
        this.innsendingsvalg = innsendingsvalg;
        return this;
    }
}
