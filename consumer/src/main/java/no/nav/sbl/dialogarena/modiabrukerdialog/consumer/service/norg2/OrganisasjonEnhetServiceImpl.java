package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.norg2;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2.OrganisasjonEnhetService;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.*;
import org.apache.commons.collections15.Transformer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static org.slf4j.LoggerFactory.getLogger;

public class OrganisasjonEnhetServiceImpl implements OrganisasjonEnhetService {

    private static final Logger logger = getLogger(OrganisasjonEnhetServiceImpl.class);

    @Inject
    private OrganisasjonEnhetV1 enhetWS;

    @Override
    public List<Arbeidsfordeling> hentArbeidsfordeling(final String enhetId) {
        WSKriterier kriterier = new WSKriterier().withEnhetId(enhetId);
        WSFinnArbeidsfordelingForEnhetBolkRequest request = new WSFinnArbeidsfordelingForEnhetBolkRequest()
                .withKriterierListe(kriterier);

        try {
            final WSFinnArbeidsfordelingForEnhetBolkResponse wsResponse = enhetWS.finnArbeidsfordelingForEnhetBolk(request);
            return getArbeidsfordelinger(wsResponse);
        } catch (FinnArbeidsfordelingForEnhetBolkUgyldigInput e) {
            logger.warn("Kall til OrganisasjonEnhetV1.finnArbeidsfordelingForEnhetBolk() kastet exception " +
                    "for enhetId=\"" + enhetId + "\".", e);
            return Collections.emptyList();
        }
    }

    private List<Arbeidsfordeling> getArbeidsfordelinger(WSFinnArbeidsfordelingForEnhetBolkResponse response) {
        Stream<WSArbeidsfordelingskriterier> wsArbeidsfordelingskriterier = getWsArbeidsfordelingskriterier(response);
        return mapToArbeidsfordeling(wsArbeidsfordelingskriterier);
    }

    private Stream<WSArbeidsfordelingskriterier> getWsArbeidsfordelingskriterier(WSFinnArbeidsfordelingForEnhetBolkResponse response) {
        Stream<WSArbeidsfordeling> arbeidsfordelinger= response.getArbeidsfordelingerForEnhetListe()
                .stream()
                .flatMap(arbeidsfordelingForEnhet -> arbeidsfordelingForEnhet.getArbeidsfordelingListe().stream());

        return arbeidsfordelinger.map(WSArbeidsfordeling::getUnderliggendeArbeidsfordelingskriterier);
    }

    private List<Arbeidsfordeling> mapToArbeidsfordeling(Stream<WSArbeidsfordelingskriterier> wsArbeidsfordelingskriterier) {
        return wsArbeidsfordelingskriterier
                .map(TIL_ARBEIDSFORDELING::transform)
                .collect(Collectors.toList());
    }

    private static final Transformer<WSArbeidsfordelingskriterier, Arbeidsfordeling> TIL_ARBEIDSFORDELING =
            wsArbeidsfordelingskriterier -> {
                final String arkivTema = wsArbeidsfordelingskriterier.getArkivtema() == null ? null : wsArbeidsfordelingskriterier.getArkivtema().getValue();
                return new Arbeidsfordeling(wsArbeidsfordelingskriterier.getGeografiskNedslagsfelt(), arkivTema);
            };

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
    public Optional<AnsattEnhet> hentEnhetGittGeografiskNedslagsfelt(final String geografiskNedslagsfelt) {
        try {
            final WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest request = new WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest();
            request.getGeografiskNedslagsfeltListe().addAll(Collections.singletonList(geografiskNedslagsfelt));
            final WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse response = enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(request);
            if (response.getEnheterForGeografiskNedslagsfeltListe() != null && !response.getEnheterForGeografiskNedslagsfeltListe().isEmpty()
                    && response.getEnheterForGeografiskNedslagsfeltListe().get(0) != null
                    && response.getEnheterForGeografiskNedslagsfeltListe().get(0).getEnhetListe() != null
                    && !response.getEnheterForGeografiskNedslagsfeltListe().get(0).getEnhetListe().isEmpty()) { // OMG!
                return optional(TIL_ANSATTENHET.transform(response.getEnheterForGeografiskNedslagsfeltListe().get(0).getEnhetListe().get(0)));
            } else {
                return none();
            }
        } catch (FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput e) {
            logger.warn("Kall til OrganisasjonEnhetV1.finnNAVKontorForGeografiskNedslagsfeltBolk() kastet exception " +
                    "for geografisknedslagsfelt=\"" + geografiskNedslagsfelt + "\".", e);
            return none();
        }
    }

    @Override
    public Optional<AnsattEnhet> hentEnhetGittEnhetId(final String enhetId) {
        try {
            final WSHentEnhetBolkRequest wsHentEnhetBolkRequest = new WSHentEnhetBolkRequest();
            wsHentEnhetBolkRequest.getEnhetIdListe().addAll(Collections.singleton(enhetId));
            final WSHentEnhetBolkResponse response = enhetWS.hentEnhetBolk(wsHentEnhetBolkRequest);
            if (response.getEnhetListe() != null && !response.getEnhetListe().isEmpty() && response.getEnhetListe().get(0) != null) {
                return optional(TIL_ANSATTENHET.transform(response.getEnhetListe().get(0)));
            } else {
                return none();
            }
        } catch (HentEnhetBolkUgyldigInput e) {
            logger.warn("Kall til OrganisasjonEnhetV1.hentEnhetGittEnhetId() kastet exception for enhetId=\"" + enhetId + "\".", e);
            return none();
        }
    }

    private static final Comparator<AnsattEnhet> ENHET_ID_STIGENDE = (o1, o2) -> o1.enhetId.compareTo(o2.enhetId);

    private static final Transformer<WSDetaljertEnhet, AnsattEnhet> TIL_ANSATTENHET =
            respons -> new AnsattEnhet(respons.getEnhetId(), respons.getNavn(), respons.getAntallRessurser());

}
