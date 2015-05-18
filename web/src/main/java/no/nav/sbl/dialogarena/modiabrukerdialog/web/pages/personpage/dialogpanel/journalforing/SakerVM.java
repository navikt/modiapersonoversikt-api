package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.SakerForTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.List;

public class SakerVM implements Serializable {

    public final IModel<Boolean> visFagsaker = Model.of(true);
    public final IModel<Boolean> visGenerelleSaker = Model.of(false);
    public final IModel<Boolean> tekniskFeil = Model.of(false);

    private SakerService sakerService;
    private String fnr;
    private Saker saker;


    public SakerVM(SakerService sakerService, String fnr) {
        this.sakerService = sakerService;
        this.fnr = fnr;
        saker = new Saker();
    }

    public final void oppdater() {
        try {
            saker = sakerService.hentSaker(fnr);
            visFagsaker.setObject(true);
            visGenerelleSaker.setObject(false);
            tekniskFeil.setObject(false);
        } catch (Exception e) {
            tekniskFeil.setObject(true);
        }
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
