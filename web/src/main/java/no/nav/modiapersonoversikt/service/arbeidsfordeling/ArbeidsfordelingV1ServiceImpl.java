package no.nav.modiapersonoversikt.service.arbeidsfordeling;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.EnhetsGeografiskeTilknytning;
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.FinnBehandlendeEnhetException;
import no.nav.modiapersonoversikt.rest.persondata.Persondata;
import no.nav.modiapersonoversikt.rest.persondata.PersondataService;
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling;
import no.nav.modiapersonoversikt.service.kodeverksmapper.KodeverksmapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArbeidsfordelingV1ServiceImpl implements ArbeidsfordelingV1Service {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsfordelingV1ServiceImpl.class);
    private final ArbeidsfordelingClient arbeidsfordelingClient;
    private final PersondataService persondataService;
    private final KodeverksmapperService kodeverksmapper;
    private final EgenAnsattService egenAnsattService;


    @Autowired
    public ArbeidsfordelingV1ServiceImpl(ArbeidsfordelingClient arbeidsfordelingClient, EgenAnsattService egenAnsattService, PersondataService persondataService, KodeverksmapperService kodeverksmapper) {
        this.arbeidsfordelingClient = arbeidsfordelingClient;
        this.persondataService = persondataService;
        this.kodeverksmapper = kodeverksmapper;
        this.egenAnsattService = egenAnsattService;
    }

    @Override
    public List<AnsattEnhet> finnBehandlendeEnhetListe(String brukerIdent, String fagomrade, String oppgavetype, String underkategori) {
        try {
            Optional<Behandling> behandling = kodeverksmapper.mapUnderkategori(underkategori);
            Persondata.Person person = persondataService.hentPerson(brukerIdent).getPerson();
            String geografiskTilknytning = person.getGeografiskTilknytning();
            String diskresjonskode = hentDiskresjonskode(person.getAdressebeskyttelse());
            boolean erEgenAnsatt = egenAnsattService.erEgenAnsatt(brukerIdent);

            if ("ANSOS_KNA".equals(underkategori)) {
                erEgenAnsatt = false;
            }
            String oppgaveTypeMapped = kodeverksmapper.mapOppgavetype(oppgavetype);

            return arbeidsfordelingClient.hentArbeidsfordeling(behandling, geografiskTilknytning, oppgaveTypeMapped, fagomrade, erEgenAnsatt, diskresjonskode);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new FinnBehandlendeEnhetException(e.getMessage(), e);
        }
    }

    private String hentDiskresjonskode(List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>> adressebeskyttelse) {
        if (adressebeskyttelse.isEmpty()) {
            return null;
        } else {
            return adressebeskyttelse.get(0).getKode().toString();
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
}
