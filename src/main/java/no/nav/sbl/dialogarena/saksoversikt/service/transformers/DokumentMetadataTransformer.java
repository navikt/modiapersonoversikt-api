package no.nav.sbl.dialogarena.saksoversikt.service.transformers;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class DokumentMetadataTransformer {

    private BulletproofKodeverkService bulletproofKodeverkService;


    private static final String DOKUMENT_LASTET_OPP = "LASTET_OPP";

    private static final Predicate<DokumentFraHenvendelse> erVedlegg
            = dokument -> !dokument.erHovedskjema();
    private static final Predicate<DokumentFraHenvendelse> erHoveddokument
            = dokument -> dokument.erHovedskjema();
    private static final Predicate<DokumentFraHenvendelse> erLastetOpp
            = dokument -> DOKUMENT_LASTET_OPP.equals(dokument.getInnsendingsvalg().name());


    public DokumentMetadataTransformer(BulletproofKodeverkService bulletproofKodeverkService) {
        this.bulletproofKodeverkService = bulletproofKodeverkService;
    }


    public DokumentMetadata dokumentMetadataFraHenvendelse(Soknad soknad) {
        String temakode = bulletproofKodeverkService.getKode(soknad.getSkjemanummerRef(), Kodeverk.Nokkel.TEMA);
        boolean kanVises = !"BID".equals(temakode);

        Dokument hovedDokument = soknad.getDokumenter()
                .stream()
                .filter(erHoveddokument)
                .map(dokument ->
                        new Dokument()
                                .withTittel(bulletproofKodeverkService.getSkjematittelForSkjemanummer(dokument.getKodeverkRef()))
                                .withKanVises(kanVises)
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokument.getArkivreferanse())
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Respons fra henvendelse inneholdt ikke hoveddokument"));

        List<Dokument> vedlegg = soknad.getDokumenter()
                .stream()
                .filter(erVedlegg)
                .filter(erLastetOpp)
                .map(dokument ->
                        new Dokument()
                                .withTittel(bulletproofKodeverkService.getSkjematittelForSkjemanummer(dokument.getKodeverkRef()))
                                .withKanVises(kanVises)
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokument.getArkivreferanse())
                )
                .collect(toList());

        ResultatWrapper temanavnForTemakode = bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA);
        return new DokumentMetadata()
                .withJournalpostId(soknad.getJournalpostId())
                .withHoveddokument(hovedDokument)
                .withEttersending(soknad.getEttersending())
                .withVedlegg(vedlegg)
                .withDato(soknad.getInnsendtDato().toGregorianCalendar().toZonedDateTime().toLocalDateTime())
                .withAvsender(Entitet.SLUTTBRUKER)
                .withMottaker(Entitet.NAV)
                .withTemakode(temakode)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withRetning(Kommunikasjonsretning.INN)
                .withTemakodeVisning((String) temanavnForTemakode.resultat);
    }
}
