package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKode;

import java.io.Serializable;

public class NyOppgave implements Serializable {
    public GsakKode.Tema tema;
    public GsakKode.OppgaveType type;
    public GsakKode.Prioritet prioritet;
    public String enhet, beskrivelse, henvendelseId;
}
