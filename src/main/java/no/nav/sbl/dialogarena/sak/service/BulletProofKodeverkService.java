package no.nav.sbl.dialogarena.sak.service;

public interface BulletProofKodeverkService {
    String ARKIVTEMA = "Arkivtemaer";
    String BEHANDLINGSTEMA = "Behandlingstema";

    String getSkjematittelForSkjemanummer(String vedleggsIdOrSkjemaId);

    boolean isEgendefinert(String vedleggsIdOrskjemaId);

    String getTemanavnForTemakode(String temakode, String kodeverk);
}
