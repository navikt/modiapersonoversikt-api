package no.nav.sbl.dialogarena.varsel.domain;

import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;

import java.util.List;

public class Varsel {

    public static final String STATUS_FERDIG = "Ferdig";
    public static final String STATUSKODE_OK = "OK";

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

    public static final Predicate<? super Varsel> VARSLER_MED_STATUS_FERDIG = new Predicate<Varsel>() {
        @Override
        public boolean evaluate(Varsel varsel) {
            return STATUS_FERDIG.equals(varsel.status);
        }
    };


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

        public static final Predicate<? super VarselMelding> VARSELMELDINGER_MED_KVITTERING_OK = new Predicate<VarselMelding>() {
            @Override
            public boolean evaluate(VarselMelding varselMelding) {
                return (STATUSKODE_OK.equals(varselMelding.statusKode)) && varselMelding.utsendingsTidspunkt != null;
            }
        };
    }
}
