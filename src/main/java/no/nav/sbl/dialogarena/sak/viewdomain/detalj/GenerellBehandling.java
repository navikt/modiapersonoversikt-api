package no.nav.sbl.dialogarena.sak.viewdomain.detalj;

import no.nav.sbl.dialogarena.common.records.Key;
import org.joda.time.DateTime;

import java.io.Serializable;

public class GenerellBehandling implements Serializable{
    public enum BehandlingsType {BEHANDLING, KVITTERING};
    public enum BehandlingsStatus {
        OPPRETTET { @Override public String cmsKey() { return "hendelse.sistoppdatert.dato"; }},
        AVSLUTTET { @Override public String cmsKey() { return "hendelse.sistoppdatert.dato"; }};

        public abstract String cmsKey();
    }

    public static final Key<DateTime> OPPRETTET_DATO = new Key<>("OPPRETTET_DATO");
    public static final Key<DateTime> BEHANDLING_DATO = new Key<>("BEHANDLING_DATO");
    public static final Key<BehandlingsStatus> BEHANDLING_STATUS = new Key<>("BEHANDLING_STATUS");
    public static final Key<BehandlingsType> BEHANDLING_TYPE = new Key<>("BEHANDLING_TYPE");
    public static final Key<Boolean> ETTERSENDING = new Key<>("ETTERSENDING");
    public static final Key<String> BEHANDLINGSTEMA = new Key<>("BEHANDLINGSTEMA");
}
