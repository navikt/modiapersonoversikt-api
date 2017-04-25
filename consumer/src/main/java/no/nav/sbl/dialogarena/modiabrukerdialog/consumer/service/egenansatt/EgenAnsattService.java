package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.egenansatt;

import no.nav.tjeneste.pip.egen.ansatt.v1.*;
import org.slf4j.Logger;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

public class EgenAnsattService {

    private static final Logger LOG = getLogger(EgenAnsattService.class);

    @Inject
    private EgenAnsattV1 egenAnsattV1;

    public boolean erEgenAnsatt(String ident) {
        final WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest request = new WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest().withIdent(ident);

        WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse wsEgenAnsatt = egenAnsattV1.hentErEgenAnsattEllerIFamilieMedEgenAnsatt(request);
        LOG.info("Kaller isEgenAnsatt");
        return wsEgenAnsatt.isEgenAnsatt();
    }
}

