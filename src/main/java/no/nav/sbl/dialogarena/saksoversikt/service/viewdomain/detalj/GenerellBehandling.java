package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj;

import no.nav.sbl.dialogarena.common.records.Key;
import org.joda.time.DateTime;

import java.io.Serializable;

public class GenerellBehandling implements Serializable{
    public enum BehandlingsType {BEHANDLING, KVITTERING};
    public enum BehandlingsStatus {
        OPPRETTET { @Override public String cmsKey() { return "hendelse.sistoppdatert.dato"; }},
        AVBRUTT { @Override public String cmsKey() { return "hendelse.sistoppdatert.dato"; }},
        AVSLUTTET { @Override public String cmsKey() { return "hendelse.sistoppdatert.dato"; }};

        public abstract String cmsKey();
    }

    public static final Key<DateTime> OPPRETTET_DATO = new Key<>("OPPRETTET_DATO");
    public static final Key<DateTime> BEHANDLING_DATO = new Key<>("BEHANDLING_DATO");
    public static final Key<BehandlingsStatus> BEHANDLING_STATUS = new Key<>("BEHANDLING_STATUS");
    public static final Key<BehandlingsType> BEHANDLINGKVITTERING = new Key<>("BEHANDLINGKVITTERING");
    public static final Key<String> BEHANDLINGS_TYPE = new Key<>("BEHANDLINGS_TYPE");
    public static final Key<Boolean> ETTERSENDING = new Key<>("ETTERSENDING");
    public static final Key<String> BEHANDLINGSTEMA = new Key<>("BEHANDLINGSTEMA");
    public static final Key<String> BEHANDLINGS_ID = new Key<>("BEHANDLINGS_ID");
    public static final Key<String> PREFIX = new Key<>("PREFIX");

}
