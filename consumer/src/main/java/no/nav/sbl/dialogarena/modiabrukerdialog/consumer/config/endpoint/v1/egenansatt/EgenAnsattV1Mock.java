package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.egenansatt;

import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse;

public class EgenAnsattV1Mock {

    public static EgenAnsattV1 egenAnsattV1() {

        return new EgenAnsattV1() {
            @Override
            public WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse hentErEgenAnsattEllerIFamilieMedEgenAnsatt(
                    WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest wsHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest) {
                WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse response =
                        new WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse();
                response.setEgenAnsatt(true);

                return response;
            }

            @Override
            public void ping() {

            }
        };
    }
}
