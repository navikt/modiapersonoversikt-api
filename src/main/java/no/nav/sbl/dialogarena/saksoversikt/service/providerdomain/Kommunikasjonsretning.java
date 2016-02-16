package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

import static no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService.*;

public enum Kommunikasjonsretning {
    INN, UT, INTERN;

    public static Kommunikasjonsretning fraJournalpostretning(String kommunikasjonsretning) {
        if (kommunikasjonsretning.equals(JOURNALPOST_INNGAAENDE)) {
            return Kommunikasjonsretning.INN;
        } else if (kommunikasjonsretning.equals(JOURNALPOST_UTGAAENDE)){
            return Kommunikasjonsretning.UT;
        } else if (kommunikasjonsretning.equals(JOURNALPOST_INTERN)){
            return Kommunikasjonsretning.INTERN;
        } else {
            throw new IllegalArgumentException(String.format("Kommunikasjonsretningen fra Journalpost er ikke definert: %s",kommunikasjonsretning));
        }
    }
}
