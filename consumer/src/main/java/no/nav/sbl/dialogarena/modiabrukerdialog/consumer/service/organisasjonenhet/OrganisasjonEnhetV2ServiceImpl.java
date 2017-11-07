package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static no.nav.modig.lang.collections.IterUtils.on;

public class OrganisasjonEnhetV2ServiceImpl implements OrganisasjonEnhetV2Service {

    private static final Logger logger = LoggerFactory.getLogger(OrganisasjonEnhetV2ServiceImpl.class);
    private static final String ORGENHET_21 = "orgEnhet_2.1";
    private static final String DEFAULT_ORGENHET21 = "false";

    @Inject
    private OrganisasjonEnhetV2 enhet;

    @Override
    public List<AnsattEnhet> hentAlleEnheter(WSOppgavebehandlerfilter oppgavebehandlerFilter) {
        final List<AnsattEnhet> enheter = new ArrayList<>();

        final HentFullstendigEnhetListeRequest request = new HentFullstendigEnhetListeRequest();
        if(valueOf(getProperty(ORGENHET_21, DEFAULT_ORGENHET21))) {
            request.setOppgavebehandlerfilter(Oppgavebehandlerfilter.fromValue(oppgavebehandlerFilter.name()));
        }
        final HentFullstendigEnhetListeResponse HentFullstendigEnhetListeResponse = enhet.hentFullstendigEnhetListe(request);

        enheter.addAll(HentFullstendigEnhetListeResponse.getEnhetListe().stream().map(TIL_ANSATTENHET).collect(Collectors.toList()));

        return on(enheter).collect(ENHET_ID_STIGENDE);
    }

    @Override
    public Optional<AnsattEnhet> hentEnhetGittEnhetId(String enhetId, WSOppgavebehandlerfilter oppgavebehandlerFilter) {
        final HentEnhetBolkRequest hentEnhetBolkRequest = new HentEnhetBolkRequest();
        final HentEnhetBolkResponse response;

        hentEnhetBolkRequest.getEnhetIdListe().addAll(Collections.singleton(enhetId));
        if(valueOf(getProperty(ORGENHET_21, DEFAULT_ORGENHET21))) {
            hentEnhetBolkRequest.setOppgavebehandlerfilter(Oppgavebehandlerfilter.fromValue(oppgavebehandlerFilter.name()));
        }
        response = enhet.hentEnhetBolk(hentEnhetBolkRequest);
        if (response.getEnhetListe() != null && !response.getEnhetListe().isEmpty() && response.getEnhetListe().get(0) != null) {
            return of(TIL_ANSATTENHET.apply(response.getEnhetListe().get(0)));
        } else {
            return empty();
        }
    }

    @Override
    public Optional<AnsattEnhet> finnNAVKontor(final String geografiskTilknytning, final String diskresjonskode) {
        final FinnNAVKontorRequest FinnNAVKontorRequest = new FinnNAVKontorRequest();
        Geografi geografi = new Geografi();
        geografi.setValue(geografiskTilknytning);
        FinnNAVKontorRequest.setGeografiskTilknytning(geografi);
        if (StringUtils.isNotBlank(diskresjonskode)) {
            Diskresjonskoder diskresjonskoder = new Diskresjonskoder();
            diskresjonskoder.setValue(diskresjonskode);
            FinnNAVKontorRequest.setDiskresjonskode(diskresjonskoder);
        }
        try {
            final FinnNAVKontorResponse FinnNAVKontorResponse = enhet.finnNAVKontor(FinnNAVKontorRequest);
            if (FinnNAVKontorResponse != null && FinnNAVKontorResponse.getNAVKontor() != null) {
                return of(TIL_ANSATTENHET.apply(FinnNAVKontorResponse.getNAVKontor()));
            } else {
                return empty();
            }
        } catch (FinnNAVKontorUgyldigInput e) {
            logger.error("Ugyldig input geografiskTilknytning=\"" + geografiskTilknytning +
                    "\", diskresjonskode=\"" + diskresjonskode + "\" til OrganisasjonEnhetV2.hentNAVKontor.", e);
            return empty();
        }
    }

    private static final Comparator<AnsattEnhet> ENHET_ID_STIGENDE = comparing(o -> o.enhetId);

    private static final Function<Organisasjonsenhet, AnsattEnhet> TIL_ANSATTENHET =
            Organisasjonsenhet -> new AnsattEnhet(
                    Organisasjonsenhet.getEnhetId(),
                    Organisasjonsenhet.getEnhetNavn(),
                    Organisasjonsenhet.getStatus().value()
            );

}
