package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.HentFullstendigEnhetListeResponse;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.HentOverordnetEnhetListeEnhetIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class OrganisasjonEnhetV2ServiceImpl implements OrganisasjonEnhetV2Service {

    private static final Logger logger = LoggerFactory.getLogger(OrganisasjonEnhetV2ServiceImpl.class);

    @Inject
    private OrganisasjonEnhetV2 organisasjonEnhetService;

    @Override
    public List<AnsattEnhet> hentAlleEnheter(WSOppgavebehandlerfilter oppgavebehandlerFilter) {
        final WSHentFullstendigEnhetListeResponse hentFullstendigEnhetListeResponse =
                organisasjonEnhetService.hentFullstendigEnhetListe(lagHentFullstendigEnhetListeRequest(oppgavebehandlerFilter));

        return hentFullstendigEnhetListeResponse.getEnhetListe().stream()
                .map(TIL_ANSATTENHET)
                .sorted(ENHET_ID_STIGENDE)
                .collect(toList());
    }

    @Override
    public Optional<AnsattEnhet> hentEnhetGittEnhetId(String enhetId, WSOppgavebehandlerfilter oppgavebehandlerFilter) {
        final WSHentEnhetBolkResponse response = organisasjonEnhetService.hentEnhetBolk(lagHentEnhetBolkRequest(enhetId, oppgavebehandlerFilter));

        if (response.getEnhetListe() != null && !response.getEnhetListe().isEmpty() && response.getEnhetListe().get(0) != null) {
            return of(TIL_ANSATTENHET.apply(response.getEnhetListe().get(0)));
        } else {
            return empty();
        }
    }

    @Override
    public Optional<AnsattEnhet> finnNAVKontor(final String geografiskTilhorighet, final String diskresjonskode) {
        final WSFinnNAVKontorRequest finnNAVKontorRequest = new WSFinnNAVKontorRequest();
        WSGeografi geografi = new WSGeografi();
        geografi.setValue(geografiskTilhorighet);
        finnNAVKontorRequest.setGeografiskTilknytning(geografi);
        if (StringUtils.isNotBlank(diskresjonskode)) {
            WSDiskresjonskoder diskresjonskoder = new WSDiskresjonskoder();
            diskresjonskoder.setValue(diskresjonskode);
            finnNAVKontorRequest.setDiskresjonskode(diskresjonskoder);
        }
        try {
            final WSFinnNAVKontorResponse FinnNAVKontorResponse = organisasjonEnhetService.finnNAVKontor(finnNAVKontorRequest);
            if (FinnNAVKontorResponse != null && FinnNAVKontorResponse.getNAVKontor() != null) {
                return of(TIL_ANSATTENHET.apply(FinnNAVKontorResponse.getNAVKontor()));
            } else {
                return empty();
            }
        } catch (FinnNAVKontorUgyldigInput e) {
            logger.error("Ugyldig input geografiskTilhorighet=\"" + geografiskTilhorighet +
                    "\", diskresjonskode=\"" + diskresjonskode + "\" til OrganisasjonEnhetV2.hentNAVKontor.", e);
            return empty();
        }
    }

    private WSHentFullstendigEnhetListeRequest lagHentFullstendigEnhetListeRequest(WSOppgavebehandlerfilter oppgavebehandlerFilter) {
        final WSHentFullstendigEnhetListeRequest request = new WSHentFullstendigEnhetListeRequest();
        request.setOppgavebehandlerfilter(no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOppgavebehandlerfilter.fromValue(oppgavebehandlerFilter.name()));

        return request;
    }

    private WSHentEnhetBolkRequest lagHentEnhetBolkRequest(String enhetId, WSOppgavebehandlerfilter oppgavebehandlerFilter) {
        WSHentEnhetBolkRequest hentEnhetBolkRequest = new WSHentEnhetBolkRequest();
        hentEnhetBolkRequest.getEnhetIdListe().addAll(Collections.singleton(enhetId));
        hentEnhetBolkRequest.setOppgavebehandlerfilter(no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOppgavebehandlerfilter.fromValue(oppgavebehandlerFilter.name()));

        return hentEnhetBolkRequest;
    }

    private static final Comparator<AnsattEnhet> ENHET_ID_STIGENDE = comparing(o -> o.enhetId);

    private static final Function<WSOrganisasjonsenhet, AnsattEnhet> TIL_ANSATTENHET =
            Organisasjonsenhet -> new AnsattEnhet(
                    Organisasjonsenhet.getEnhetId(),
                    Organisasjonsenhet.getEnhetNavn(),
                    Organisasjonsenhet.getStatus().value()
            );
}
