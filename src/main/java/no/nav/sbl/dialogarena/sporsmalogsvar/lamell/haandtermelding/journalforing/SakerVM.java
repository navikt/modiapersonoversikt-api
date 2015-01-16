package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.SakerForTema;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.List;

public class SakerVM implements Serializable {

    public final IModel<Boolean> visFagsaker = Model.of(true);
    public final IModel<Boolean> visGenerelleSaker = Model.of(false);

    private InnboksVM innboksVM;
    private Saker saker;

    private SakerService sakerService;

    public SakerVM(InnboksVM innboksVM, SakerService sakerService) {
        this.innboksVM = innboksVM;
        this.sakerService = sakerService;
        saker = new Saker();
    }

    public final void oppdater() {
        saker = sakerService.hentSaker(innboksVM.getFnr());
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
        return saker.getSakerListeFagsak().sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }

    public List<SakerForTema> getGenerelleSakerGruppertPaaTema() {
        return saker.getSakerListeGenerelle().sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }
}
