package no.nav.modiapersonoversikt.legacy.varsel.domain;

import org.joda.time.DateTime;

import java.util.List;

public class Varsel {

    public final String varselType;
    public final DateTime mottattTidspunkt;
    public final List<VarselMelding> meldingListe;
    public final boolean erRevarsling;

    public Varsel(String varselType, DateTime mottattTidspunkt, List<VarselMelding> meldingListe, boolean erRevarsling) {
        this.varselType = varselType;
        this.mottattTidspunkt = mottattTidspunkt;
        this.meldingListe = meldingListe;
        this.erRevarsling = erRevarsling;
    }

    public static class VarselMelding {
        public final String kanal;
        public final String innhold;
        public final String mottakerInformasjon;
        public final DateTime utsendingsTidspunkt;
        public final String feilbeskrivelse;
        public final String epostemne;
        public final String url;
        public final boolean erRevarsel;

        public VarselMelding(String kanal, String innhold, String mottakerInformasjon, DateTime utsendingsTidspunkt, String feilbeskrivelse, String epostemne, String url, boolean erRevarsel) {
            this.kanal = kanal;
            this.innhold = innhold;
            this.mottakerInformasjon = mottakerInformasjon;
            this.utsendingsTidspunkt = utsendingsTidspunkt;
            this.feilbeskrivelse = feilbeskrivelse;
            this.epostemne = epostemne;
            this.url = url;
            this.erRevarsel = erRevarsel;
        }

    }
}
