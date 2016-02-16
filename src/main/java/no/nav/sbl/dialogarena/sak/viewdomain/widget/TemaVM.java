package no.nav.sbl.dialogarena.sak.viewdomain.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.sak.service.interfaces.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;

import javax.inject.Inject;
import java.io.Serializable;

import static no.nav.sbl.dialogarena.sak.service.interfaces.BulletProofKodeverkService.ARKIVTEMA;

public class TemaVM implements FeedItemVM, Serializable {

    public String temakode;
    public GenerellBehandling sistoppdaterteBehandling;
    public String temanavn;


    @Inject
    private BulletProofKodeverkService kodeverk;

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
        this.temanavn =  kodeverk.getTemanavnForTemakode(temakode, ARKIVTEMA);
        return this;
    }

    public TemaVM withSistOppdaterteBehandling(GenerellBehandling behandling) {
        sistoppdaterteBehandling = behandling;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TemaVM temaVM = (TemaVM) o;

        if (!temakode.equals(temaVM.temakode)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return temakode.hashCode();
    }
}
