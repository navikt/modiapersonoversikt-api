package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.SakerForTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.List;

public class SakerVM implements Serializable {

    public final IModel<Boolean> visFagsaker = Model.of(true);
    public final IModel<Boolean> visGenerelleSaker = Model.of(false);

    private SakerService sakerService;
    private String fnr;

    private Saker saker;

    public SakerVM(SakerService sakerService, String fnr) {
        this.sakerService = sakerService;
        this.fnr = fnr;
        saker = new Saker();
        oppdater();
    }

    public final void oppdater() {
        saker = sakerService.hentSaker(fnr);
        visFagsaker.setObject(true);
        visGenerelleSaker.setObject(false);
    }

    public AbstractReadOnlyModel<Boolean> sakerFinnes() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return saker.sakerFinnes();
            }
        };
    }

    public List<SakerForTema> getFagsakerGruppertPaaTema() {
        return saker.getSakerListeFagsak().sorter();
    }

    public List<SakerForTema> getGenerelleSakerGruppertPaaTema() {
        return saker.getSakerListeGenerelle().sorter();
    }
}
