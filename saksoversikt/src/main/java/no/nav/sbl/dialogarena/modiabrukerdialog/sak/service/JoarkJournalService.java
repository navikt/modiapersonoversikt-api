package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.JournalV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import javax.inject.Inject;
import java.util.List;

public class JoarkJournalService {

    @Inject
    private JournalV2Service journalV2Service;

    @Inject
    private InnsynJournalV2Service innsynJournalV2Service;

    public ResultatWrapper<List<DokumentMetadata>> hentTilgjengeligeJournalposter(List<Sak> saker, String fnr) {
        return journalV2Service.hentTilgjengeligJournalpostListe(saker, fnr);

    }

    public TjenesteResultatWrapper hentDokument(String dokumentId, String journalpostId) {
        return journalV2Service.hentDokument(journalpostId, dokumentId);
    }

    public ResultatWrapper<DokumentMetadata> identifiserJournalpost(String behandlingsId){
        return innsynJournalV2Service.identifiserJournalpost(behandlingsId);
    }

}
