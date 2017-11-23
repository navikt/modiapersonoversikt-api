package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.WSFinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.WSFinnBehandlendeEnhetListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class ArbeidsfordelingV1Service {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsfordelingV1Service.class);
    private final ArbeidsfordelingV1 arbeidsfordeling;
    private final PersonKjerneinfoServiceBi personService;
    private final KodeverksmapperService kodeverksmapper;

    @Inject
    public ArbeidsfordelingV1Service(ArbeidsfordelingV1 arbeidsfordeling, PersonKjerneinfoServiceBi personService, KodeverksmapperService kodeverksmapper) {
        this.arbeidsfordeling = arbeidsfordeling;
        this.personService = personService;
        this.kodeverksmapper = kodeverksmapper;
    }

    public List<String> finnBehandlendeEnhetListe(String brukerIdent, String fagomrade, String oppgavetype, String underkategori) {
        try {
            Optional<Behandling> behandling = kodeverksmapper.mapUnderkategori(underkategori);
            String geografiskTilknytning = hentGeografiskTilknytning(brukerIdent);
            WSFinnBehandlendeEnhetListeResponse response = arbeidsfordeling.finnBehandlendeEnhetListe(new WSFinnBehandlendeEnhetListeRequest()
                    .withArbeidsfordelingKriterier(new WSArbeidsfordelingKriterier()
                            .withBehandlingstema(new WSBehandlingstema()
                                    .withValue(behandling.map(Behandling::getBehandlingstema).orElse(null)))
                            .withBehandlingstype(new WSBehandlingstyper()
                                    .withValue(behandling.map(Behandling::getBehandlingstype).orElse(null)))
                            .withOppgavetype(new WSOppgavetyper()
                                    .withValue(kodeverksmapper.mapOppgavetype(oppgavetype)))
                            .withTema(new WSTema()
                                    .withValue(fagomrade))
                            .withGeografiskTilknytning(new WSGeografi()
                                    .withValue((geografiskTilknytning)))));
            return response.getBehandlendeEnhetListe().stream()
                    .map(WSOrganisasjonsenhet::getEnhetId)
                    .collect(Collectors.toList());
        } catch (FinnBehandlendeEnhetListeUgyldigInput e) {
            LOG.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private String hentGeografiskTilknytning(String brukerIdent) {
        return Optional.of(personService.hentKjerneinformasjon(new HentKjerneinformasjonRequest(brukerIdent)))
                .map(HentKjerneinformasjonResponse::getPerson)
                .map(Person::getPersonfakta)
                .map(Personfakta::getGeografiskTilknytning)
                .map(GeografiskTilknytning::getValue)
                .orElse(null);
    }
}
