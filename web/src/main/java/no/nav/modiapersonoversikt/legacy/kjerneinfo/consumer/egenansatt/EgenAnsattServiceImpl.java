package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt;

import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class EgenAnsattServiceImpl implements EgenAnsattService {

    private static final Logger LOG = getLogger(EgenAnsattServiceImpl.class);
    private EgenAnsattV1 egenAnsattV1;

    public EgenAnsattServiceImpl(EgenAnsattV1 egenAnsattV1){
        this.egenAnsattV1 = egenAnsattV1;
    }

    public boolean erEgenAnsatt(String ident) {

        final WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest request =
                new WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest().withIdent(ident);

        LOG.info("Kaller egenAnsattV1.hentErEgenAnsattEllerIFamilieMedEgenAnsatt");
        WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse response =
                egenAnsattV1.hentErEgenAnsattEllerIFamilieMedEgenAnsatt(request);

        return response.isEgenAnsatt();
    }

}

