package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.norg2;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2.OrganisasjonEnhetService;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.OrganisasjonEnhetV1;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.WSDetaljertEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSHentFullstendigEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSHentFullstendigEnhetListeResponse;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.modig.lang.collections.IterUtils.on;

public class OrganisasjonEnhetServiceImpl implements OrganisasjonEnhetService {

    @Inject
    private OrganisasjonEnhetV1 enhetWS;

    @Override
    public List<AnsattEnhet> hentAlleEnheter() {
        final List<AnsattEnhet> enheter = new ArrayList<>();

        final WSHentFullstendigEnhetListeRequest request = new WSHentFullstendigEnhetListeRequest();
        request.setInkluderNedlagte(false);
        final WSHentFullstendigEnhetListeResponse wsHentFullstendigEnhetListeResponse = enhetWS.hentFullstendigEnhetListe(request);

        enheter.addAll(wsHentFullstendigEnhetListeResponse.getEnhetListe().stream().map(TIL_ANSATTENHET::transform).collect(Collectors.toList()));

        return on(enheter).collect(ENHET_ID_STIGENDE);
    }

    @Override
    public AnsattEnhet hentEnhet(String geografiskNedslagsfelt) {
        try {
            final WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest request = new WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest();
            request.getGeografiskNedslagsfeltListe().addAll(Collections.singletonList(geografiskNedslagsfelt));
            return TIL_ANSATTENHET.transform(enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(request).getEnheterForGeografiskNedslagsfeltListe().get(0).getEnhetListe().get(0));
        } catch (FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput e) {
            throw new RuntimeException(e);
        }
    }

    private static final Comparator<AnsattEnhet> ENHET_ID_STIGENDE = (o1, o2) -> o1.enhetId.compareTo(o2.enhetId);

    private static final Transformer<WSDetaljertEnhet, AnsattEnhet> TIL_ANSATTENHET =
            respons -> new AnsattEnhet(respons.getEnhetId(), respons.getNavn());
}
