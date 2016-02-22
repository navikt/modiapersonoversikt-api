package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.Innsyn;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v2.HentJournalpostListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v2.Journal_v2PortType;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSFagsystemer;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentResponse;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentJournalpostListeRequest;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper.Feilmelding.DOKUMENT_IKKE_FUNNET;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper.Feilmelding.SIKKERHETSBEGRENSNING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper.Feilmelding.UKJENT_FEIL;
import static org.slf4j.LoggerFactory.getLogger;

public class InnsynImpl implements Innsyn {

    @Inject
    private Journal_v2PortType joarkV2;

    private static final Logger logger = getLogger(InnsynImpl.class);

    @Override
    public TjenesteResultatWrapper hentTilgjengeligJournalpostListe(List<Sak> saker) {
        WSHentJournalpostListeRequest wsRequest = new WSHentJournalpostListeRequest();
        wsRequest.getSakListe().addAll(sakerTilJoarkSak(saker));

        try {
            return new TjenesteResultatWrapper(Java8Utils.optional(joarkV2.hentJournalpostListe(wsRequest).getJournalpostListe().stream()));
        } catch (HentJournalpostListeSikkerhetsbegrensning e) {
            logger.warn("Sikkerhetsbegrensning ved henting av dokument! {}", e.getMessage());
            return new TjenesteResultatWrapper(SIKKERHETSBEGRENSNING);
        } catch (RuntimeException e) {
            logger.error("Soapfault: ", e);
            return new TjenesteResultatWrapper(UKJENT_FEIL);
        }
    }

    @Override
    public TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse) {
        WSHentDokumentRequest wsRequest = new WSHentDokumentRequest();
        wsRequest.setDokumentId(dokumentreferanse);
        wsRequest.setJournalpostId(journalpostid);

        try {
            WSHentDokumentResponse wsResponse = joarkV2.hentDokument(wsRequest);
            return new TjenesteResultatWrapper(wsResponse.getDokument());
        } catch (HentDokumentSikkerhetsbegrensning e) {
            logger.warn("Dokumentet med journalpostid '{}' og dokumentid '{}' ble ikke funnet. {}", journalpostid, dokumentreferanse, e.getMessage());
            return new TjenesteResultatWrapper(DOKUMENT_IKKE_FUNNET);
        } catch (HentDokumentDokumentIkkeFunnet e) {
            logger.warn("Dokumentet med journalpostid '{}' og dokumentid '{}' ble ikke hentet grunnet en sikkerhetsbegrensning. {}", journalpostid, dokumentreferanse, e.getMessage());
            return new TjenesteResultatWrapper(SIKKERHETSBEGRENSNING);
        } catch (RuntimeException e) {
            logger.error("Det skjedde en ukjent feil under henting av dokumentet med journalpostid '{}' og dokumentid '{}'.", journalpostid, dokumentreferanse, e);
            return new TjenesteResultatWrapper(UKJENT_FEIL);
        }
    }


    private List<WSSak> sakerTilJoarkSak(List<Sak> saker) {
        return saker.stream()
                .map(sak -> {
                    WSSak joarkSak = new WSSak();
                    joarkSak.setSakId(sak.getSaksId());
                    WSFagsystemer fagsystemer = new WSFagsystemer();
                    fagsystemer.setValue(sak.getFagsystem());
                    joarkSak.setFagsystem(fagsystemer);
                    return joarkSak;
                }).collect(toList());
    }
}
