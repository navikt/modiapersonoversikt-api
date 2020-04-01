package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling;

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.EnhetsGeografiskeTilknytning;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.FinnBehandlendeEnhetException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ArbeidsfordelingV1ServiceImpl implements ArbeidsfordelingV1Service {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsfordelingV1ServiceImpl.class);
    private final ArbeidsfordelingV1 arbeidsfordeling;
    private final ArbeidsfordelingClient arbeidsfordelingClient;
    private final PersonKjerneinfoServiceBi personService;
    private final KodeverksmapperService kodeverksmapper;
    private final UnleashService unleashService;
    private final EgenAnsattService egenAnsattService;


    @Inject
    public ArbeidsfordelingV1ServiceImpl(ArbeidsfordelingV1 arbeidsfordeling, ArbeidsfordelingClient arbeidsfordelingClient, EgenAnsattService egenAnsattService, PersonKjerneinfoServiceBi personService, KodeverksmapperService kodeverksmapper, UnleashService unleashService) {
        this.arbeidsfordeling = arbeidsfordeling;
        this.arbeidsfordelingClient = arbeidsfordelingClient;
        this.personService = personService;
        this.kodeverksmapper = kodeverksmapper;
        this.unleashService = unleashService;
        this.egenAnsattService = egenAnsattService;
    }

    @Override
    public List<AnsattEnhet> finnBehandlendeEnhetListe(String brukerIdent, String fagomrade, String oppgavetype, String underkategori) {
        try {
            Optional<Behandling> behandling = kodeverksmapper.mapUnderkategori(underkategori);
            GeografiskTilknytning geografiskTilknytning = personService.hentGeografiskTilknytning(brukerIdent);
            boolean erEgenAnsatt = egenAnsattService.erEgenAnsatt(brukerIdent);
            if("ANSOS_KNA".equals(underkategori)) {
                erEgenAnsatt = false;
            }
            String oppgaveTypeMapped = kodeverksmapper.mapOppgavetype(oppgavetype);

            if (unleashService.isEnabled(Feature.ARBEIDSFORDELING_REST)) {
                return arbeidsfordelingClient.hentArbeidsfordeling(behandling, geografiskTilknytning, oppgaveTypeMapped, fagomrade, erEgenAnsatt);
            }
            return hentAnsattEnhetViaSOAP(behandling, geografiskTilknytning, oppgavetype, fagomrade);

        } catch (FinnBehandlendeEnhetListeUgyldigInput | IOException | RuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw new FinnBehandlendeEnhetException(e.getMessage(), e);
        }
    }

    @Override
    public List<EnhetsGeografiskeTilknytning> hentGTnummerForEnhet(String valgtEnhet) {
        try {
            return arbeidsfordelingClient.hentGTForEnhet(valgtEnhet);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public List<AnsattEnhet> hentAnsattEnhetViaSOAP(Optional<Behandling> behandling, GeografiskTilknytning geografiskTilknytning, String oppgavetype, String fagomrade) throws IOException, FinnBehandlendeEnhetListeUgyldigInput {
        Behandlingstema behandlingstema = new Behandlingstema();
        behandling.ifPresent((value) -> behandlingstema.setValue(value.getBehandlingstema()));

        Behandlingstyper behandlingstype = new Behandlingstyper();
        behandling.ifPresent((value) -> behandlingstype.setValue(value.getBehandlingstype()));

        Diskresjonskoder diskresjonskoder = new Diskresjonskoder();
        diskresjonskoder.setValue(geografiskTilknytning.getDiskresjonskode());

        Oppgavetyper oppgavetyper = new Oppgavetyper();
        oppgavetyper.setValue(kodeverksmapper.mapOppgavetype(oppgavetype));

        Geografi geografi = new Geografi();
        geografi.setValue(geografiskTilknytning.getValue());

        Tema tema = new Tema();
        tema.setValue(fagomrade);

        ArbeidsfordelingKriterier fordelingsKriterier = new ArbeidsfordelingKriterier();
        fordelingsKriterier.setBehandlingstema(behandlingstema);
        fordelingsKriterier.setBehandlingstype(behandlingstype);
        fordelingsKriterier.setDiskresjonskode(diskresjonskoder);
        fordelingsKriterier.setOppgavetype(oppgavetyper);
        fordelingsKriterier.setTema(tema);
        fordelingsKriterier.setGeografiskTilknytning(geografi);

        FinnBehandlendeEnhetListeRequest request = new FinnBehandlendeEnhetListeRequest();
        request.setArbeidsfordelingKriterier(fordelingsKriterier);

        FinnBehandlendeEnhetListeResponse response = arbeidsfordeling.finnBehandlendeEnhetListe(request);
        return response.getBehandlendeEnhetListe().stream()
                .map(wsEnhet -> new AnsattEnhet(wsEnhet.getEnhetId(), wsEnhet.getEnhetNavn()))
                .collect(toList());
    }

}
