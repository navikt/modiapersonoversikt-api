package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;

import java.util.List;

public class Kvittering extends GenerellBehandling {
    public static final Key<List<Record<Dokument>>> INNSENDTE_DOKUMENTER = new Key<>("INNSENDTE_DOKUMENTER");
    public static final Key<List<Record<Dokument>>> MANGLENDE_DOKUMENTER = new Key<>("MANGLENDE_DOKUMENTER");
    public static final Key<String> BEHANDLINGS_ID = new Key<>("BEHANDLINGS_ID");
    public static final Key<String> BEHANDLINGSKJEDE_ID = new Key<>("BEHANDLINGSKJEDE_ID");
    public static final Key<String> SKJEMANUMMER_REF = new Key<>("SKJEMANUMMER_REF");
}