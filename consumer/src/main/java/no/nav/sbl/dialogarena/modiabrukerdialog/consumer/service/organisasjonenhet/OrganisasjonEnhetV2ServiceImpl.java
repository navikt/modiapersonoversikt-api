package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static no.nav.modig.lang.collections.IterUtils.on;

public class OrganisasjonEnhetV2ServiceImpl implements OrganisasjonEnhetV2Service {

    private static final Logger logger = LoggerFactory.getLogger(OrganisasjonEnhetV2ServiceImpl.class);

    @Inject
    private OrganisasjonEnhetV2 enhetWS;

    @Override
    public List<AnsattEnhet> hentAlleEnheter() {
        final List<AnsattEnhet> enheter = new ArrayList<>();

        final WSHentFullstendigEnhetListeRequest request = new WSHentFullstendigEnhetListeRequest();
        final WSHentFullstendigEnhetListeResponse wsHentFullstendigEnhetListeResponse = enhetWS.hentFullstendigEnhetListe(request);

        enheter.addAll(wsHentFullstendigEnhetListeResponse.getEnhetListe().stream().map(TIL_ANSATTENHET).collect(Collectors.toList()));

        return on(enheter).collect(ENHET_ID_STIGENDE);
    }

    @Override
    public Optional<AnsattEnhet> hentEnhetGittEnhetId(String enhetId) {
        final WSHentEnhetBolkRequest wsHentEnhetBolkRequest = new WSHentEnhetBolkRequest();
        wsHentEnhetBolkRequest.getEnhetIdListe().addAll(Collections.singleton(enhetId));
        final WSHentEnhetBolkResponse response;
        response = enhetWS.hentEnhetBolk(wsHentEnhetBolkRequest);
        if (response.getEnhetListe() != null && !response.getEnhetListe().isEmpty() && response.getEnhetListe().get(0) != null) {
            return of(TIL_ANSATTENHET.apply(response.getEnhetListe().get(0)));
        } else {
            return empty();
        }
    }

    @Override
    public Optional<AnsattEnhet> finnNAVKontor(final String geografiskTilknytning, final String diskresjonskode) {
        final WSFinnNAVKontorRequest wsFinnNAVKontorRequest = new WSFinnNAVKontorRequest();
        wsFinnNAVKontorRequest.setGeografiskTilknytning(new WSGeografiskeOmraader().withValue(geografiskTilknytning));
        if (StringUtils.isNotBlank(diskresjonskode)) {
            wsFinnNAVKontorRequest.setDiskresjonskode(new WSDiskresjonskoder().withValue(diskresjonskode));
        }
        try {
            final WSFinnNAVKontorResponse wsFinnNAVKontorResponse = enhetWS.finnNAVKontor(wsFinnNAVKontorRequest);
            if (wsFinnNAVKontorResponse != null && wsFinnNAVKontorResponse.getNAVKontor() != null) {
                return of(TIL_ANSATTENHET.apply(wsFinnNAVKontorResponse.getNAVKontor()));
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

    private static final Function<WSOrganisasjonsenhet, AnsattEnhet> TIL_ANSATTENHET =
            wsOrganisasjonsenhet -> new AnsattEnhet(
                    wsOrganisasjonsenhet.getEnhetId(),
                    wsOrganisasjonsenhet.getEnhetNavn(),
                    wsOrganisasjonsenhet.getStatus().value()
            );

}
