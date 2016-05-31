package no.nav.sbl.dialogarena.sak.viewdomain.detalj;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.sak.viewdomain.HenvendelseType;

import java.util.List;

public class Kvittering extends GenerellBehandling {
    public static final Key<List<Record<Dokument>>> INNSENDTE_DOKUMENTER = new Key<>("INNSENDTE_DOKUMENTER");
    public static final Key<List<Record<Dokument>>> MANGLENDE_DOKUMENTER = new Key<>("MANGLENDE_DOKUMENTER");
    public static final Key<String> BEHANDLINGSKJEDE_ID = new Key<>("BEHANDLINGSKJEDE_ID");
    public static final Key<String> JOURNALPOST_ID = new Key<>("JOURNALPOST_ID");
    public static final Key<HenvendelseType> KVITTERINGSTYPE = new Key<>("KVITTERINGSTYPE");
    public static final Key<String> SKJEMANUMMER_REF = new Key<>("SKJEMANUMMER_REF");
    public static final Key<Optional<String>> ARKIVREFERANSE_ORIGINALKVITTERING = new Key<>("ARKIVREFERANSE_ORIGINALKVITTERING");
}