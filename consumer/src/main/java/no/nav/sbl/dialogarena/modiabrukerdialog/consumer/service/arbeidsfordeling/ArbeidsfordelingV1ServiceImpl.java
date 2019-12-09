package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.FinnBehandlendeEnhetException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ArbeidsfordelingV1ServiceImpl implements ArbeidsfordelingV1Service {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsfordelingV1ServiceImpl.class);
    private final ArbeidsfordelingV1 arbeidsfordeling;
    private final PersonKjerneinfoServiceBi personService;
    private final KodeverksmapperService kodeverksmapper;

    @Inject
    public ArbeidsfordelingV1ServiceImpl(ArbeidsfordelingV1 arbeidsfordeling, PersonKjerneinfoServiceBi personService, KodeverksmapperService kodeverksmapper) {
        this.arbeidsfordeling = arbeidsfordeling;
        this.personService = personService;
        this.kodeverksmapper = kodeverksmapper;
    }

    @Override
    public List<AnsattEnhet> finnBehandlendeEnhetListe(String brukerIdent, String fagomrade, String oppgavetype, String underkategori) {
        try {
            Optional<Behandling> behandling = kodeverksmapper.mapUnderkategori(underkategori);
            GeografiskTilknytning geografiskTilknytning = personService.hentGeografiskTilknytning(brukerIdent);

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
        } catch (FinnBehandlendeEnhetListeUgyldigInput | IOException | RuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw new FinnBehandlendeEnhetException(e.getMessage(), e);
        }
    }

}
