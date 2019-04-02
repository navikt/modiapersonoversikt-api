package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.DokumentMetadataTransformer;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Java8Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.JOURNALFORT_ANNET_TEMA;

@SuppressWarnings("squid:S1166")
public class DokumentMetadataService {

    private static final String DOKMOT_TEMA_REGEX = "BIL|FOR";

    private InnsynJournalV2Service innsynJournalV2Service;
    private HenvendelseService henvendelseService;
    private DokumentMetadataTransformer dokumentMetadataTransformer;
    private SafService safService;

    private List<DokumentMetadata> journalposter;
    private List<DokumentMetadata> soknader;
    private Set<Baksystem> feilendeBaksystem;

    public DokumentMetadataService(InnsynJournalV2Service innsynJournalV2Service,
                                   HenvendelseService henvendelseService,
                                   DokumentMetadataTransformer dokumentMetadataTransformer,
                                   SafService safService) {

        this.innsynJournalV2Service = innsynJournalV2Service;
        this.henvendelseService = henvendelseService;
        this.dokumentMetadataTransformer = dokumentMetadataTransformer;
        this.safService = safService;

    }

    public ResultatWrapper<List<DokumentMetadata>> hentDokumentMetadata(String fnr) {
        initFields();

        hentJournalposter(fnr);
        hentSoknader(fnr);

        populerDokmotSoknaderMedJournalpostIdFraJoark();

        leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksisterer();

        Stream<DokumentMetadata> innsendteSoknaderSomHarEndretTema = finnSoknaderSomHarForskjelligTemaISoknadOgJournalpost();

        Stream<DokumentMetadata> innsendteSoknaderSomBareIkkeHarJournalpost = finnSoknaderUtenJournalpost();

        markerJournalposterSomEttersendingOmSoknadErEttersending();

        return new ResultatWrapper<>(Java8Utils.concat(
                journalposter.stream(),
                innsendteSoknaderSomBareIkkeHarJournalpost,
                innsendteSoknaderSomHarEndretTema
        ).collect(toList()), feilendeBaksystem);
    }

    private void initFields() {
        journalposter = new ArrayList<>();
        soknader = new ArrayList<>();
        feilendeBaksystem = new HashSet<>();
    }

    private void populerDokmotSoknaderMedJournalpostIdFraJoark() {
        soknader.stream()
                .filter(soknad -> soknad.getJournalpostId() == null)
                .filter(soknad -> soknad.getTemakode().matches(DOKMOT_TEMA_REGEX))
                .forEach(soknad -> soknad.withJournalpostId(finnJournalpostIdFraBehandlingsId(soknad.getBehandlingsId())));
    }

    private String finnJournalpostIdFraBehandlingsId(String behandlingsId) {
        try {
            ResultatWrapper<DokumentMetadata> wrapper = innsynJournalV2Service.identifiserJournalpost(behandlingsId);
            feilendeBaksystem.addAll(wrapper.feilendeSystemer);
            return wrapper.resultat != null ? wrapper.resultat.getJournalpostId() : null;
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
            return null;
        }
    }

    private void hentSoknader(String fnr) {
        try {
            soknader.addAll(henvendelseService.hentInnsendteSoknader(fnr)
                    .stream()
                    .map(soknad -> dokumentMetadataTransformer.dokumentMetadataFraHenvendelseSoknader(soknad))
                    .collect(toList()));
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
        }
    }

    private void hentJournalposter(String fnr) {
        ResultatWrapper<List<DokumentMetadata>> journalpostWrapper = safService.hentJournalposter(fnr);
        journalposter.addAll(journalpostWrapper.resultat);
        feilendeBaksystem.addAll(journalpostWrapper.feilendeSystemer);
    }

    private void leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksisterer() {
        journalposter
                .stream()
                .filter(jp -> journalpostHarSoknad(jp, soknader))
                .forEach(jp -> jp.withBaksystem(Baksystem.HENVENDELSE));
    }

    private boolean journalpostHarSoknad(DokumentMetadata jp, List<DokumentMetadata> innsendteSoknader) {
        return innsendteSoknader.stream().anyMatch(soknad -> soknadLikJournalpost(soknad, jp));
    }

    private Stream<DokumentMetadata> finnSoknaderSomHarForskjelligTemaISoknadOgJournalpost() {
        return soknader
                .stream()
                .filter(this::harJournalforingEndretTema)
                .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(JOURNALFORT_ANNET_TEMA));
    }

    private boolean harJournalforingEndretTema(DokumentMetadata soknad) {
        return journalposter
                .stream()
                .filter(jp -> soknadLikJournalpost(soknad, jp))
                .anyMatch(jp -> !jp.getTemakode().equals(soknad.getTemakode()));
    }

    private Stream<DokumentMetadata> finnSoknaderUtenJournalpost() {
        return soknader
                .stream()
                .filter(this::finnesIkkeJournalpost)
                .map(sokand -> sokand.withIsJournalfort(FALSE).withLeggTilEttersendelseTekstDersomEttersendelse());
    }

    private boolean finnesIkkeJournalpost(DokumentMetadata soknad) {
        return journalposter.stream()
                .noneMatch(jp -> soknadLikJournalpost(soknad, jp));
    }

    private boolean soknadLikJournalpost(DokumentMetadata soknad, DokumentMetadata jp) {
        return jp.getJournalpostId().equals(soknad.getJournalpostId());
    }

    private void markerJournalposterSomEttersendingOmSoknadErEttersending() {
        journalposter.forEach(journalpost -> {
            boolean erEttersending = soknader
                    .stream()
                    .anyMatch(soknad -> soknadLikJournalpost(soknad, journalpost) && soknad.isEttersending());

            journalpost.withEttersending(erEttersending);
        });
    }
}
