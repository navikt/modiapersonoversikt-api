package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt;

import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument;
import org.joda.time.DateTime;

import java.util.List;

public class Soknad {

    public enum HenvendelseStatus {UNDER_ARBEID, FERDIG}

    public static final Key<String> BEHANDLINGS_ID = new Key<>("BEHANDLINGS_ID");
    public static final Key<String> BEHANDLINGSKJEDE_ID = new Key<>("BEHANDLINGSKJEDE_ID");
    public static final Key<String> JOURNALPOST_ID = new Key<>("JOURNALPOST_ID");
    public static final Key<HenvendelseStatus> STATUS = new Key<>("STATUS");
    public static final Key<DateTime> OPPRETTET_DATO = new Key<>("OPPRETTET_DATO");
    public static final Key<DateTime> INNSENDT_DATO = new Key<>("INNSENDT_DATO");
    public static final Key<DateTime> SISTENDRET_DATO = new Key<>("SISTENDRET_DATO");
    public static final Key<String> SKJEMANUMMER_REF = new Key<>("SKJEMANUMMER_REF");
    public static final Key<Boolean> ETTERSENDING = new Key<>("ETTERSENDING");
    public static final Key<List<Record<Dokument>>> DOKUMENTER = new Key<>("DOKUMENTER");
    public static final Key<HenvendelseType> TYPE = new Key<>("TYPE");

}
