package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.HentTilgjengeligJournalpostListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.InnsynJournalV1;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Fagsystemer;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.meldinger.HentDokumentRequest;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.meldinger.HentDokumentResponse;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.meldinger.HentTilgjengeligJournalpostListeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

public class InnsynJournalService {

    @Inject
    private InnsynJournalV1 innsynJournalV1;

    private static final Logger logger = LoggerFactory.getLogger(InnsynJournalService.class);

    private List<Sak> sakerTilJoarkSak(List<no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak> saker) {
        return saker.stream()
                .map(sak -> {
                    Sak joarkSak = new Sak();
                    joarkSak.setSakId(sak.getSaksId());
                    Fagsystemer fagsystemer = new Fagsystemer();
                    fagsystemer.setValue(sak.getFagsystem());
                    joarkSak.setFagsystem(fagsystemer);
                    return joarkSak;
                }).collect(toList());
    }

    public Optional<Stream<Journalpost>> joarkSakhentTilgjengeligeJournalposter(List<no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak> saker) {
        try {
            HentTilgjengeligJournalpostListeRequest request = new HentTilgjengeligJournalpostListeRequest();
            request.getSakListe().addAll(sakerTilJoarkSak(saker));
            request.setMerkInnsynDokument(true);
            return Java8Utils.optional(innsynJournalV1.hentTilgjengeligJournalpostListe(request).getJournalpostListe().stream());
        } catch (HentTilgjengeligJournalpostListeSikkerhetsbegrensning hentTilgjengeligJournalpostListeSikkerhetsbegrensning) {
            logger.warn("Sikkerhetsbegrensning ved henting av dokument!" + hentTilgjengeligJournalpostListeSikkerhetsbegrensning);
            return empty();
        } catch (SOAPFaultException e) {
            logger.error("Soupfault: " + e);
            return empty();
        } catch (WebServiceException e) {
            logger.error("Feil ved sending av melding: " + e);
            return empty();
        }
    }

    public TjenesteResultatWrapper hentDokument(String dokumentId, String journalpostId){
        HentDokumentRequest hentDokumentRequest = new HentDokumentRequest();
        hentDokumentRequest.setDokumentId(dokumentId);
        hentDokumentRequest.setJournalpostId(journalpostId);

        try {
            HentDokumentResponse response = innsynJournalV1.hentDokument(hentDokumentRequest);
            return new TjenesteResultatWrapper(response.getDokument());
        } catch (HentDokumentDokumentIkkeFunnet e) {
                logger.warn("Dokumentet med journalpostid '{}' og dokumentid '{}' ble ikke funnet. {}", journalpostId, dokumentId, e.getMessage());
                return new TjenesteResultatWrapper(TjenesteResultatWrapper.Feilmelding.DOKUMENT_IKKE_FUNNET);
            } catch (HentDokumentSikkerhetsbegrensning e) {
                logger.warn("Dokumentet med journalpostid '{}' og dokumentid '{}' ble ikke hentet grunnet en sikkerhetsbegrensning. {}", journalpostId, dokumentId, e.getMessage());
                return new TjenesteResultatWrapper(TjenesteResultatWrapper.Feilmelding.SIKKERHETSBEGRENSNING);
            } catch (Exception e) {
                logger.error("Det skjedde en ukjent feil under henting av dokumentet med journalpostid '{}' og dokumentid '{}'.", journalpostId, dokumentId, e);
                return new TjenesteResultatWrapper(TjenesteResultatWrapper.Feilmelding.UKJENT_FEIL);
            }
    }
}
