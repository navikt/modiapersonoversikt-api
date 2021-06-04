package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.FeilendeBaksystemException;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;
import no.nav.modiapersonoversikt.legacy.sak.transformers.DokumentMetadataTransformer;
import no.nav.modiapersonoversikt.legacy.sak.utils.Java8Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.Feilmelding.JOURNALFORT_ANNET_TEMA;

@SuppressWarnings("squid:S1166")
public class DokumentMetadataService {

    private static final String DOKMOT_TEMA_REGEX = "BIL|FOR";

    private InnsynJournalV2Service innsynJournalV2Service;
    private HenvendelseService henvendelseService;
    private DokumentMetadataTransformer dokumentMetadataTransformer;
    private SafService safService;

    public DokumentMetadataService(InnsynJournalV2Service innsynJournalV2Service,
                                   HenvendelseService henvendelseService,
                                   DokumentMetadataTransformer dokumentMetadataTransformer,
                                   SafService safService) {

        this.innsynJournalV2Service = innsynJournalV2Service;
        this.henvendelseService = henvendelseService;
        this.dokumentMetadataTransformer = dokumentMetadataTransformer;
        this.safService = safService;
    }

    public synchronized ResultatWrapper<List<DokumentMetadata>> hentDokumentMetadata(String fnr) {
        DokumentMetadataResultat resultat = new DokumentMetadataResultat();

        hentJournalposter(fnr, resultat);   // Populerer journalposter med data fra SAF
        hentSoknader(fnr, resultat);        // Populerer soknader med data fra henvendelse

        populerDokmotSoknaderMedJournalpostIdFraJoark(resultat); // Augmenterer soknader med data fra joark

        leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksisterer(resultat); // Augmenterer journalposter med data fra henvendelse

        Stream<DokumentMetadata> innsendteSoknaderSomHarEndretTema = finnSoknaderSomHarForskjelligTemaISoknadOgJournalpost(resultat);

        Stream<DokumentMetadata> innsendteSoknaderSomBareIkkeHarJournalpost = finnSoknaderUtenJournalpost(resultat);

        markerJournalposterSomEttersendingOmSoknadErEttersending(resultat);

        return new ResultatWrapper<>(Java8Utils.concat(
                resultat.journalposter.stream(),
                innsendteSoknaderSomBareIkkeHarJournalpost,
                innsendteSoknaderSomHarEndretTema
        ).collect(toList()), resultat.feilendeBaksystem);
    }


    private void populerDokmotSoknaderMedJournalpostIdFraJoark(DokumentMetadataResultat resultat) {
        resultat.soknader.stream()
                .filter(soknad -> soknad.getJournalpostId() == null)
                .filter(soknad -> soknad.getTemakode().matches(DOKMOT_TEMA_REGEX))
                .forEach(soknad -> soknad.withJournalpostId(finnJournalpostIdFraBehandlingsId(soknad.getBehandlingsId(), resultat)));
    }

    private String finnJournalpostIdFraBehandlingsId(String behandlingsId, DokumentMetadataResultat resultat) {
        try {
            ResultatWrapper<DokumentMetadata> wrapper = innsynJournalV2Service.identifiserJournalpost(behandlingsId);
            resultat.feilendeBaksystem.addAll(wrapper.feilendeSystemer);
            return wrapper.resultat != null ? wrapper.resultat.getJournalpostId() : null;
        } catch (FeilendeBaksystemException e) {
            resultat.feilendeBaksystem.add(e.getBaksystem());
            return null;
        }
    }

    private void hentSoknader(String fnr, DokumentMetadataResultat resultat) {
        try {
            resultat.soknader.addAll(henvendelseService.hentInnsendteSoknader(fnr)
                    .stream()
                    .map(soknad -> dokumentMetadataTransformer.dokumentMetadataFraHenvendelseSoknader(soknad))
                    .collect(toList()));
        } catch (FeilendeBaksystemException e) {
            resultat.feilendeBaksystem.add(e.getBaksystem());
        }
    }

    private void hentJournalposter(String fnr, DokumentMetadataResultat resultat) {
        ResultatWrapper<List<DokumentMetadata>> journalpostWrapper = safService.hentJournalposter(fnr);
        resultat.journalposter.addAll(journalpostWrapper.resultat);
        resultat.feilendeBaksystem.addAll(journalpostWrapper.feilendeSystemer);
    }

    private void leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksisterer(DokumentMetadataResultat resultat) {
        resultat.journalposter
                .stream()
                .filter(jp -> journalpostHarSoknad(jp, resultat.soknader))
                .forEach(jp -> jp.withBaksystem(Baksystem.HENVENDELSE));
    }

    private boolean journalpostHarSoknad(DokumentMetadata jp, List<DokumentMetadata> innsendteSoknader) {
        return innsendteSoknader.stream().anyMatch(soknad -> soknadLikJournalpost(soknad, jp));
    }

    private Stream<DokumentMetadata> finnSoknaderSomHarForskjelligTemaISoknadOgJournalpost(DokumentMetadataResultat resultat) {
        return resultat.soknader
                .stream()
                .filter(soknad -> this.harJournalforingEndretTema(soknad, resultat))
                .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(JOURNALFORT_ANNET_TEMA));
    }

    private boolean harJournalforingEndretTema(DokumentMetadata soknad, DokumentMetadataResultat resultat) {
        return resultat.journalposter
                .stream()
                .filter(jp -> soknadLikJournalpost(soknad, jp))
                .anyMatch(jp -> !jp.getTemakode().equals(soknad.getTemakode()));
    }

    private Stream<DokumentMetadata> finnSoknaderUtenJournalpost(DokumentMetadataResultat resultat) {
        return resultat.soknader
                .stream()
                .filter(soknad -> this.finnesIkkeJournalpost(soknad, resultat))
                .map(sokand -> sokand.withIsJournalfort(FALSE).withLeggTilEttersendelseTekstDersomEttersendelse());
    }

    private boolean finnesIkkeJournalpost(DokumentMetadata soknad, DokumentMetadataResultat resultat) {
        return resultat.journalposter.stream()
                .noneMatch(jp -> soknadLikJournalpost(soknad, jp));
    }

    private boolean soknadLikJournalpost(DokumentMetadata soknad, DokumentMetadata jp) {
        return jp.getJournalpostId().equals(soknad.getJournalpostId());
    }

    private void markerJournalposterSomEttersendingOmSoknadErEttersending(DokumentMetadataResultat resultat) {
        resultat.journalposter.forEach(journalpost -> {
            boolean erEttersending = resultat.soknader
                    .stream()
                    .anyMatch(soknad -> soknadLikJournalpost(soknad, journalpost) && soknad.isEttersending());

            journalpost.withEttersending(erEttersending);
        });
    }
}


class DokumentMetadataResultat {
    List<DokumentMetadata> journalposter;
    List<DokumentMetadata> soknader;
    Set<Baksystem> feilendeBaksystem;

    DokumentMetadataResultat() {
        journalposter = new ArrayList<>();
        soknader = new ArrayList<>();
        feilendeBaksystem = new HashSet<>();
    }
}
