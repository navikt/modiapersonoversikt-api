package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.norg2;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2.OrganisasjonEnhetService;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.FinnArbeidsfordelingForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.OrganisasjonEnhetV1;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnArbeidsfordelingForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnArbeidsfordelingForEnhetBolkResponse;
import org.apache.commons.collections15.Transformer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

}
