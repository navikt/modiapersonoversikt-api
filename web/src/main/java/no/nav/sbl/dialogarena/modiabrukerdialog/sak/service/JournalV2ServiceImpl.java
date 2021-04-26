package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.JournalV2Service;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSVariantformater;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentResponse;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.*;
import static org.slf4j.LoggerFactory.getLogger;

//Preserve stacktrace
@SuppressWarnings("squid:S1166")
public class JournalV2ServiceImpl implements JournalV2Service {

    @Autowired
    private JournalV2 journalV2;

    private static final Logger logger = getLogger(JournalV2ServiceImpl.class);

    public TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse) {
        WSHentDokumentRequest wsRequest = new WSHentDokumentRequest();
        wsRequest.setDokumentId(dokumentreferanse);
        wsRequest.setJournalpostId(journalpostid);
        wsRequest.setVariantformat(new WSVariantformater()
                .withKodeRef("http://nav.no/kodeverk/Term/Variantformater/ARKIV/nb/Arkiv?v=1")
                .withValue("ARKIV")
        );

        try {
            WSHentDokumentResponse wsResponse = journalV2.hentDokument(wsRequest);
            return new TjenesteResultatWrapper(wsResponse.getDokument());
        } catch (HentDokumentSikkerhetsbegrensning e) {
            logger.warn("Dokumentet med journalpostid '{}' og dokumentid '{}' ble ikke hentet grunnet en sikkerhetsbegrensning. {}", journalpostid, dokumentreferanse, e.getMessage());
            return new TjenesteResultatWrapper(SIKKERHETSBEGRENSNING);
        } catch (HentDokumentDokumentIkkeFunnet e) {
            logger.warn("Dokumentet med journalpostid '{}' og dokumentid '{}' ble ikke funnet. {}", journalpostid, dokumentreferanse, e.getMessage());
            return new TjenesteResultatWrapper(DOKUMENT_IKKE_FUNNET);
        } catch (RuntimeException e) {
            logger.error("Det skjedde en ukjent feil under henting av dokumentet med journalpostid '{}' og dokumentid '{}'.", journalpostid, dokumentreferanse, e);
            return new TjenesteResultatWrapper(UKJENT_FEIL);
        }
    }
}
