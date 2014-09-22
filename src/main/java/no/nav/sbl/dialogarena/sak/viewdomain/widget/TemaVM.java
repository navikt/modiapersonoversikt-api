package no.nav.sbl.dialogarena.sak.viewdomain.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;

import java.io.Serializable;

public class TemaVM implements FeedItemVM, Serializable {

    public String temakode;
    public GenerellBehandling sistoppdaterteBehandling;

    @Override
    public String getType() {
        return "sakstema";
    }

    @Override
    public String getId() {
        return temakode;
    }

    public TemaVM withTemaKode(String temakode) {
        this.temakode = temakode;
        return this;
    }

    public TemaVM withSistOppdaterteBehandling(GenerellBehandling behandling) {
        sistoppdaterteBehandling = behandling;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemaVM temaVM = (TemaVM) o;

        if (!temakode.equals(temaVM.temakode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return temakode.hashCode();
    }
}
