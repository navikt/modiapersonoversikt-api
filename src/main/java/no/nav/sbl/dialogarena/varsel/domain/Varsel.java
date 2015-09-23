package no.nav.sbl.dialogarena.varsel.domain;

import org.joda.time.DateTime;

import java.util.List;

public class Varsel {
    public final String varselType;
    public final DateTime mottattTidspunkt;
    public final String status;
    public final List<VarselMelding> meldingListe;

    public Varsel(String varselType, DateTime mottattTidspunkt, String status, List<VarselMelding> meldingListe) {
        this.varselType = varselType;
        this.mottattTidspunkt = mottattTidspunkt;
        this.status = status;
        this.meldingListe = meldingListe;
    }

    public static class VarselMelding {
        public final String kanal;
        public final String innhold;
        public final String mottakerInformasjon;
        public final DateTime utsendingsTidspunkt;
        public final String statusKode;
        public final String feilbeskrivelse;
        public final String epostemne;
        public final String url;

        public VarselMelding(String kanal, String innhold, String mottakerInformasjon, DateTime utsendingsTidspunkt, String statusKode, String feilbeskrivelse, String epostemne, String url) {
            this.kanal = kanal;
            this.innhold = innhold;
            this.mottakerInformasjon = mottakerInformasjon;
            this.utsendingsTidspunkt = utsendingsTidspunkt;
            this.statusKode = statusKode;
            this.feilbeskrivelse = feilbeskrivelse;
            this.epostemne = epostemne;
            this.url = url;
        }
    }
}
