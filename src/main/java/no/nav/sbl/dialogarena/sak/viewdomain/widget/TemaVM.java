package no.nav.sbl.dialogarena.sak.viewdomain.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;

public class TemaVM implements FeedItemVM {

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

}
