package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.HentEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class OrganisasjonEnhetV2ServiceImpl implements OrganisasjonEnhetV2Service {

    @Inject
    private OrganisasjonEnhetV2 enhetWS;

    @Override
    public List<AnsattEnhet> hentAlleEnheter() {
        final List<AnsattEnhet> enheter = new ArrayList<>();

        final WSHentFullstendigEnhetListeRequest request = new WSHentFullstendigEnhetListeRequest();
        final WSHentFullstendigEnhetListeResponse wsHentFullstendigEnhetListeResponse = enhetWS.hentFullstendigEnhetListe(request);

        enheter.addAll(wsHentFullstendigEnhetListeResponse.getEnhetListe().stream().map(TIL_ANSATTENHET::transform).collect(Collectors.toList()));

        return on(enheter).collect(ENHET_ID_STIGENDE);
    }

    @Override
    public Optional<AnsattEnhet> hentEnhetGittEnhetId(String enhetId) {
        final WSHentEnhetBolkRequest wsHentEnhetBolkRequest = new WSHentEnhetBolkRequest();
        wsHentEnhetBolkRequest.getEnhetIdListe().addAll(Collections.singleton(enhetId));
        final WSHentEnhetBolkResponse response;
        try {
            response = enhetWS.hentEnhetBolk(wsHentEnhetBolkRequest);
            if (response.getEnhetListe() != null && !response.getEnhetListe().isEmpty() && response.getEnhetListe().get(0) != null) {
                return optional(TIL_ANSATTENHET.transform(response.getEnhetListe().get(0)));
            } else {
                return none();
            }
        } catch (HentEnhetBolkUgyldigInput hentEnhetBolkUgyldigInput) {
            return none();
        }

    }

    private static final Comparator<AnsattEnhet> ENHET_ID_STIGENDE = (o1, o2) -> o1.enhetId.compareTo(o2.enhetId);

    private static final Transformer<WSOrganisasjonsenhet, AnsattEnhet> TIL_ANSATTENHET =
            respons -> new AnsattEnhet(respons.getEnhetId(), respons.getEnhetNavn(), respons.getStatus().value());

}
