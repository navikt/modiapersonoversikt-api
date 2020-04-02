package no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.BulletproofKodeverkService;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class DokumentMetadataTransformer {

    private static final String DOKUMENT_LASTET_OPP = "LASTET_OPP";
    private static final String TEMAKODE_BIDRAG = "BID";

    private BulletproofKodeverkService bulletproofKodeverkService;

    public DokumentMetadataTransformer(BulletproofKodeverkService bulletproofKodeverkService) {
        this.bulletproofKodeverkService = bulletproofKodeverkService;
    }

    public DokumentMetadata dokumentMetadataFraHenvendelseSoknader(Soknad soknad) {
        String temakode = getTemakode(soknad.getSkjemanummerRef());
        ResultatWrapper temanavn = getTemanavn(temakode);

        Dokument hovedDokument = lagHovedDokument(soknad, kanVises(temakode));
        List<Dokument> vedlegg = lagVedlegg(soknad, kanVises(temakode));

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
                .withTemakodeVisning((String) temanavn.resultat)
                .withBehandlingsId(soknad.getBehandlingsId());
    }

    private ResultatWrapper<String> getTemanavn(String temakode) {
        return bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA);
    }

    private String getTemakode(String skjemanummerRef) {
        return bulletproofKodeverkService.getKode(skjemanummerRef, Kodeverk.Nokkel.TEMA);
    }

    private boolean kanVises(String temakode) {
        return !TEMAKODE_BIDRAG.equals(temakode);
    }

    private List<Dokument> lagVedlegg(Soknad soknad, boolean kanVises) {
        return soknad.getDokumenter()
                .stream()
                .filter(DokumentMetadataTransformer::erVedlegg)
                .filter(DokumentMetadataTransformer::erLastetOpp)
                .map(dokument -> freHenvendelseDokumentTilDokument(dokument, kanVises))
                .collect(toList());
    }

    private static boolean erVedlegg(DokumentFraHenvendelse dokument) {
        return !dokument.erHovedskjema();
    }

    private static boolean erLastetOpp(DokumentFraHenvendelse dokument) {
        return DOKUMENT_LASTET_OPP.equals(dokument.getInnsendingsvalg().name());
    }

    private Dokument lagHovedDokument(Soknad soknad, boolean kanVises) {
        return soknad.getDokumenter()
                .stream()
                .filter(DokumentMetadataTransformer::erHoveddokument)
                .map(dokument -> freHenvendelseDokumentTilDokument(dokument, kanVises))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Respons fra henvendelse inneholdt ikke hoveddokument"));
    }

    private static boolean erHoveddokument(DokumentFraHenvendelse dokument) {
        return dokument.erHovedskjema();
    }

    private Dokument freHenvendelseDokumentTilDokument(DokumentFraHenvendelse dokument, boolean kanVises) {
        return new Dokument()
                .withTittel(dokument.getTilleggstittel())
                .withKanVises(kanVises)
                .withLogiskDokument(false)
                .withDokumentreferanse(dokument.getArkivreferanse());
    }
}
