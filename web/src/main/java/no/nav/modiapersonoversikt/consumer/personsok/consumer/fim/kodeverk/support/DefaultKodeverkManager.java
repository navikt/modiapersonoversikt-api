package no.nav.modiapersonoversikt.consumer.personsok.consumer.fim.kodeverk.support;

import no.nav.modiapersonoversikt.consumer.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DefaultKodeverkManager implements KodeverkManager {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private KodeverkServiceDelegate serviceDelegate;

    public DefaultKodeverkManager(KodeverkServiceDelegate serviceDelegate) {
        this.serviceDelegate = serviceDelegate;
    }

    @Override
    public String getBeskrivelseForKode(String kodeValue, String kodeverkNavn, String spraak) {
        if (isBlank(kodeValue) || isBlank(kodeverkNavn)) {
            return EMPTY;
        }
        XMLHentKodeverkRequest request = new XMLHentKodeverkRequest();
        request.setNavn(kodeverkNavn);
        XMLEnkeltKodeverk kodeverk = (XMLEnkeltKodeverk) serviceDelegate.hentKodeverk(request).getKodeverk();

        List<XMLKode> kodeListe = kodeverk.getKode();

        for (XMLKode kode : kodeListe) {
            if (kode.getNavn().equals(kodeValue) && !kode.getTerm().isEmpty() && kode.getTerm().get(0).getSpraak().equals(spraak)) {
                return kode.getTerm().get(0).getNavn();
            }
        }
        return kodeValue;
    }
}
