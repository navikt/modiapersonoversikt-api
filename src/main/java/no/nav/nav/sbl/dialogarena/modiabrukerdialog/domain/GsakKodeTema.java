package no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Tema kodeverk. Nøstet kodeverk for uthenting fra gsak
 */

public abstract class GsakKodeTema implements Serializable {

    public final String kode;
    public final String tekst;

    protected GsakKodeTema(String kode, String tekst) {
        this.kode = kode;
        this.tekst = tekst;
    }

    public static class Tema extends GsakKodeTema implements Serializable {
        public final List<OppgaveType> oppgaveTyper;
        public final List<Prioritet> prioriteter;

        public Tema(String kode, String tekst, List<OppgaveType> oppgaveTyper, List<Prioritet> prioritets) {
            super(kode, tekst);
            this.oppgaveTyper = unmodifiableList(oppgaveTyper);
            this.prioriteter = unmodifiableList(prioritets);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("kode", kode)
                    .append("tekst", tekst)
                    .append("oppgaveTyper", oppgaveTyper)
                    .toString();
        }
    }

    public static class OppgaveType extends GsakKodeTema implements Serializable {
        public final Integer dagerFrist;

        public OppgaveType(String kode, String tekst, Integer dagerFrist) {
            super(kode, tekst);
            this.dagerFrist = dagerFrist;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("kode", kode)
                    .append("tekst", tekst)
                    .append("dagerFrist", dagerFrist)
                    .toString();
        }
    }

    public static class Prioritet extends GsakKodeTema implements Serializable {

        public Prioritet(String kode, String tekst) {
            super(kode, tekst);
        }
    }

}
