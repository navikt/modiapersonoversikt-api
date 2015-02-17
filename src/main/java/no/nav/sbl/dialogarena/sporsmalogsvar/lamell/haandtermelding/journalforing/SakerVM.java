package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.SakerForTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.List;

public class SakerVM implements Serializable {

    public static final String TEMAGRUPPE_OVRG = "OVRG";

    public final IModel<Boolean> visFagsaker = Model.of(true);
    public final IModel<Boolean> visGenerelleSaker = Model.of(false);

    private InnboksVM innboksVM;
    private Saker saker;

    private SakerService sakerService;

    public SakerVM(InnboksVM innboksVM, SakerService sakerService) {
        this.innboksVM = innboksVM;
        this.sakerService = sakerService;
        visFagsaker.setObject(!valgtTraadsTemagruppeErOvrige(innboksVM));
        visGenerelleSaker.setObject(valgtTraadsTemagruppeErOvrige(innboksVM));
        saker = new Saker();
    }

    public final void oppdater() {
        saker = sakerService.hentSaker(innboksVM.getFnr());
        visFagsaker.setObject(!valgtTraadsTemagruppeErOvrige(innboksVM));
        visGenerelleSaker.setObject(valgtTraadsTemagruppeErOvrige(innboksVM));
    }

    private boolean valgtTraadsTemagruppeErOvrige(InnboksVM innboksVM) {
         return innboksVM.getTraader().size() > 0 && innboksVM.getValgtTraad().getEldsteMelding().getTemagruppeKey().equals(TEMAGRUPPE_OVRG);
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
