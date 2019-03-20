package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.DokumentMetadataTransformer;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Java8Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private List<DokumentMetadata> safJournalposter;
    private List<DokumentMetadata> henvendelseSoknader;
    private Set<Baksystem> feilendeBaksystem;

    private static final Logger LOG = LoggerFactory.getLogger(DokumentMetadataService.class);

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

        hentSafJournalposter(fnr);
        hentHenvendelseSoknader(fnr);

        populerDokmotSoknaderMedJournalpostIdFraJoark();

        leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksistererIHenvendelse();

        Stream<DokumentMetadata> innsendteSoknaderSomHarEndretTema = finnSoknaderSomHarForskjelligTemaIHenvendelseOgSaf();

        Stream<DokumentMetadata> innsendteSoknaderSomBareFinnesIHenvendelse = finnSoknaderBareIHenvendelse();

        markerJournalposterSomEttersendingOmSoknadErEttersending();

        return new ResultatWrapper<>(Java8Utils.concat(
                safJournalposter.stream(),
                innsendteSoknaderSomBareFinnesIHenvendelse,
                innsendteSoknaderSomHarEndretTema
        ).collect(toList()), feilendeBaksystem);
    }

    private void initFields() {
        safJournalposter = new ArrayList<>();
        henvendelseSoknader = new ArrayList<>();
        feilendeBaksystem = new HashSet<>();
    }

    private void populerDokmotSoknaderMedJournalpostIdFraJoark() {
        henvendelseSoknader.stream()
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

    private void hentHenvendelseSoknader(String fnr) {
        try {
            henvendelseSoknader.addAll(henvendelseService.hentInnsendteSoknader(fnr)
                    .stream()
                    .map(soknad -> dokumentMetadataTransformer.dokumentMetadataFraHenvendelse(soknad))
                    .collect(toList()));
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
        }
    }

    private void hentSafJournalposter(String fnr) {
        try {
            List<DokumentMetadata> dokumentMetadata = safService.hentJournalposter(fnr);
            safJournalposter.addAll(dokumentMetadata);
        } catch (RuntimeException e) {
            feilendeBaksystem.add(Baksystem.SAF);
            LOG.error("Feil i henting av journalposter fra SAF" ,e);
        }
    }

    private void leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksistererIHenvendelse() {
        safJournalposter
                .stream()
                .filter(jp -> journalpostEksistererIHenvendelse(jp, henvendelseSoknader))
                .forEach(jp -> jp.withBaksystem(Baksystem.HENVENDELSE));
    }

    private boolean journalpostEksistererIHenvendelse(DokumentMetadata jp, List<DokumentMetadata> innsendteSoknaderIHenvendelse) {
        return innsendteSoknaderIHenvendelse.stream().anyMatch(soknad -> soknadLikJournalpost(soknad, jp));
    }

    private Stream<DokumentMetadata> finnSoknaderSomHarForskjelligTemaIHenvendelseOgSaf() {
        return henvendelseSoknader
                .stream()
                .filter(this::harJournalforingEndretTema)
                .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(JOURNALFORT_ANNET_TEMA));
    }

    private boolean harJournalforingEndretTema(DokumentMetadata henvendelseSoknad) {
        return safJournalposter
                .stream()
                .filter(jp -> soknadLikJournalpost(henvendelseSoknad, jp))
                .anyMatch(jp -> !jp.getTemakode().equals(henvendelseSoknad.getTemakode()));
    }

    private Stream<DokumentMetadata> finnSoknaderBareIHenvendelse() {
        return henvendelseSoknader
                .stream()
                .filter(this::finnesIkkeISaf)
                .map(sokand -> sokand.withIsJournalfort(FALSE).withLeggTilEttersendelseTekstDersomEttersendelse());
    }

    private boolean finnesIkkeISaf(DokumentMetadata soknad) {
        return safJournalposter.stream()
                .noneMatch(jp -> soknadLikJournalpost(soknad, jp));
    }

    private boolean soknadLikJournalpost(DokumentMetadata henvendelseMetadata, DokumentMetadata jp) {
        return jp.getJournalpostId().equals(henvendelseMetadata.getJournalpostId());
    }

    private void markerJournalposterSomEttersendingOmSoknadErEttersending() {
        safJournalposter.forEach(journalpost -> {
            boolean erEttersending = henvendelseSoknader
                    .stream()
                    .anyMatch(soknad -> soknadLikJournalpost(soknad, journalpost) && soknad.isEttersending());

            journalpost.withEttersending(erEttersending);
        });
    }
}
