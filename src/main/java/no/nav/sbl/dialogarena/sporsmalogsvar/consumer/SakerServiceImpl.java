package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.*;

import javax.inject.Inject;
import java.util.List;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.utils.SakerUtils.hentGenerelleOgIkkeGenerelleSaker;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.utils.SakerUtils.leggTilFagsystemnavnOgTemanavn;

public class SakerServiceImpl implements SakerService {

    @Inject
    private GsakService gsakService;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private StandardKodeverk standardKodeverk;
    @Inject
    private LokaltKodeverk lokaltKodeverk;


    @Override
    public Saker hentSaker(String fnr) {
        List<Sak> sakerForBruker = gsakService.hentSakerForBruker(fnr);
        leggTilFagsystemnavnOgTemanavn(sakerForBruker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);
        return hentGenerelleOgIkkeGenerelleSaker(sakerForBruker, lokaltKodeverk);
    }

}
