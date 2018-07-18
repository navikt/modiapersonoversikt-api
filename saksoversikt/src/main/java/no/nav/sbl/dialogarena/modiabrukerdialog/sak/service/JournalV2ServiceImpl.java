package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.JournalpostTransformer;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.JournalV2Service;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v2.HentJournalpostListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSFagsystemer;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSSoekeFilter;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSVariantformater;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentResponse;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentJournalpostListeRequest;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.JOARK_SIKKERHETSBEGRENSNING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.*;
import static no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSJournalFiltrering.KUN_GYLDIGE_OG_FERDIGSTILTE_FORSENDELSER_OG_DOKUMENTER;
import static org.slf4j.LoggerFactory.getLogger;

//Preserve stacktrace
@SuppressWarnings("squid:S1166")
public class JournalV2ServiceImpl implements JournalV2Service {

    @Inject
    private JournalV2 journalV2;

    @Inject
    private JournalpostTransformer journalpostTransformer;

    private static final Logger logger = getLogger(JournalV2ServiceImpl.class);

    public ResultatWrapper<List<DokumentMetadata>> hentTilgjengeligJournalpostListe(List<Sak> saker, String fnr) {
        Map<String, Sak> saksMap = saker.stream().collect(toMap(Sak::getSaksId, Function.identity()));

        WSHentJournalpostListeRequest wsRequest = new WSHentJournalpostListeRequest();
        wsRequest.setSoekeFilter(new WSSoekeFilter().withJournalFiltrering(KUN_GYLDIGE_OG_FERDIGSTILTE_FORSENDELSER_OG_DOKUMENTER));
        wsRequest.getSakListe().addAll(sakerTilJoarkSak(saker));

        if (wsRequest.getSakListe().isEmpty()) {
            return new ResultatWrapper<>(emptyList());
        }

        try {
            List<ResultatWrapper<DokumentMetadata>> dokumentMetadataWrappers = journalV2.hentJournalpostListe(wsRequest)
                    .getJournalpostListe()
                    .stream()
                    .map(jp -> journalpostTransformer.dokumentMetadataFraJournalPost(jp, fnr, saksMap.get(jp.getGjelderSak().getSakId()).getFagsaksnummer()))
                    .collect(toList());

            List<DokumentMetadata> dokumentMetadata = dokumentMetadataWrappers.stream()
                    .map(jpw -> jpw.resultat)
                    .collect(toList());
            Set<Baksystem> feilendeBaksystemer = dokumentMetadataWrappers.stream().map(entry -> entry.feilendeSystemer).flatMap(Collection::stream).collect(toSet());

            return new ResultatWrapper<>(dokumentMetadata, feilendeBaksystemer);
        } catch (HentJournalpostListeSikkerhetsbegrensning e) {
            logger.warn("Sikkerhetsbegrensning ved henting av dokument! {}", e.getMessage());
            Set<Baksystem> feil = new HashSet<>();
            feil.add(JOARK_SIKKERHETSBEGRENSNING);
            return new ResultatWrapper<>(emptyList(), feil);
        } catch (RuntimeException e) {
            logger.error("Soapfault: ", e);
            throw new FeilendeBaksystemException(Baksystem.JOARK);
        }
    }

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
