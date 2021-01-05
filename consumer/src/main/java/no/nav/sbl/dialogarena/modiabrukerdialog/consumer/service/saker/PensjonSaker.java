package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.SakerUtils.leggTilFagsystemnavnOgTemanavn;

public class PensjonSaker {

    @Autowired
    private PsakService psakService;
    @Autowired
    private GsakKodeverk gsakKodeverk;
    @Autowired
    private StandardKodeverk standardKodeverk;

    public List<Sak> hentPensjonSaker(String fnr) {
        List<Sak> saker = psakService.hentSakerFor(fnr);
        leggTilFagsystemnavnOgTemanavn(saker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);
        return saker;
    }

}
