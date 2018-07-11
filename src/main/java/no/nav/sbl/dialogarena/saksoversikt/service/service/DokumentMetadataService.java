package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.transformers.DokumentMetadataTransformer;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.JOURNALFORT_ANNET_TEMA;

@SuppressWarnings("squid:S1166")
public class DokumentMetadataService {

    private static final String DOKMOT_TEMA_REGEX = "BIL|FOR";

    private JoarkJournalService joarkJournalService;
    private HenvendelseService henvendelseService;
    private DokumentMetadataTransformer dokumentMetadataTransformer;

    private List<DokumentMetadata> joarkJournalposter;
    private List<DokumentMetadata> henvendelseSoknader;
    private Set<Baksystem> feilendeBaksystem;

    public DokumentMetadataService(JoarkJournalService joarkJournalService,
                                   HenvendelseService henvendelseService,
                                   DokumentMetadataTransformer dokumentMetadataTransformer) {
        this.joarkJournalService = joarkJournalService;
        this.henvendelseService = henvendelseService;
        this.dokumentMetadataTransformer = dokumentMetadataTransformer;

    }

    public ResultatWrapper<List<DokumentMetadata>> hentDokumentMetadata(List<Sak> saker, String fnr) {
        initFields();

        hentJoarkJournalposter(saker, fnr);
        hentHenvendelseSoknader(fnr);

        populerDokmotSoknaderMedJournalpostIdFraJoark();

        leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksistererIHenvendelse();

        Stream<DokumentMetadata> innsendteSoknaderSomHarEndretTema = finnSoknaderSomHarForskjelligTemaIHenvendelseOgJoark();

        Stream<DokumentMetadata> innsendteSoknaderSomBareFinnesIHenvendelse = finnSoknaderBareIHenvendelse();

        markerJournalposterSomEttersendingOmSoknadErEttersending();

        return new ResultatWrapper<>(Java8Utils.concat(
                joarkJournalposter.stream(),
                innsendteSoknaderSomBareFinnesIHenvendelse,
                innsendteSoknaderSomHarEndretTema
        ).collect(toList()), feilendeBaksystem);
    }

    private void initFields() {
        joarkJournalposter = new ArrayList<>();
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
            ResultatWrapper<DokumentMetadata> wrapper = joarkJournalService.identifiserJournalpost(behandlingsId);
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

    private void hentJoarkJournalposter(List<Sak> saker, String fnr) {
        try {
            ResultatWrapper<List<DokumentMetadata>> dokumentMetadataResultatWrapper = joarkJournalService.hentTilgjengeligeJournalposter(saker, fnr);
            joarkJournalposter.addAll(dokumentMetadataResultatWrapper.resultat);
            feilendeBaksystem.addAll(dokumentMetadataResultatWrapper.feilendeSystemer);
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
        }
    }

    private void leggTilHenvendelseSomBaksystemIJournalposterOmSoknadEksistererIHenvendelse() {
        joarkJournalposter
                .stream()
                .filter(jp -> journalpostEksistererIHenvendelse(jp, henvendelseSoknader))
                .forEach(jp -> jp.withBaksystem(Baksystem.HENVENDELSE));
    }

    private boolean journalpostEksistererIHenvendelse(DokumentMetadata jp, List<DokumentMetadata> innsendteSoknaderIHenvendelse) {
        return innsendteSoknaderIHenvendelse.stream().anyMatch(henvendelse -> henvendelseLikJournalpost(henvendelse, jp));
    }

    private Stream<DokumentMetadata> finnSoknaderSomHarForskjelligTemaIHenvendelseOgJoark() {
        return henvendelseSoknader
                .stream()
                .filter(henvendelseDokumentmetadata -> harJournalforingEndretTema(henvendelseDokumentmetadata, joarkJournalposter))
                .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(JOURNALFORT_ANNET_TEMA));
    }

    private boolean harJournalforingEndretTema(DokumentMetadata henvendelseDokumentMetadata, List<DokumentMetadata> joarkDokumentMetadataListe) {
        return joarkDokumentMetadataListe
                .stream()
                .filter(dokumentMetadata -> henvendelseLikJournalpost(henvendelseDokumentMetadata, dokumentMetadata))
                .anyMatch(dokumentMetadata -> !dokumentMetadata.getTemakode().equals(henvendelseDokumentMetadata.getTemakode()));
    }

    private Stream<DokumentMetadata> finnSoknaderBareIHenvendelse() {
        return henvendelseSoknader
                .stream()
                .filter(this::finnesIkkeIJoark)
                .map(dokumentMetadata -> dokumentMetadata.withIsJournalfort(FALSE).withLeggTilEttersendelseTekstDersomEttersendelse());
    }

    private boolean finnesIkkeIJoark(DokumentMetadata soknad) {
        return joarkJournalposter.stream()
                .noneMatch(jp -> henvendelseLikJournalpost(soknad, jp));
    }

    private boolean henvendelseLikJournalpost(DokumentMetadata henvendelseMetadata, DokumentMetadata jp) {
        return jp.getJournalpostId().equals(henvendelseMetadata.getJournalpostId());
    }

    private void markerJournalposterSomEttersendingOmSoknadErEttersending() {
        joarkJournalposter.forEach(journalpost -> {
            boolean erEttersending = henvendelseSoknader
                    .stream()
                    .anyMatch(henvendelse -> henvendelseLikJournalpost(henvendelse, journalpost) && henvendelse.isEttersending());

            journalpost.withEttersending(erEttersending);
        });
    }
}
