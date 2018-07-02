package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.*;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.*;
import org.springframework.context.annotation.Bean;

public class InnsynJournalV2PortTypeMock {

    @Bean
    public static InnsynJournalV2 createInnsynJournalV2PortTypeMock(){
        return new InnsynJournalV2() {
            @Override
            public HentDokumentResponse hentDokument(HentDokumentRequest hentDokumentRequest) throws HentDokumentDokumentIkkeFunnet, HentDokumentSikkerhetsbegrensning {
                return null;
            }

            @Override
            public void ping() {

            }

            @Override
            public IdentifiserJournalpostResponse identifiserJournalpost(IdentifiserJournalpostRequest identifiserJournalpostRequest) throws IdentifiserJournalpostJournalpostIkkeInngaaende, IdentifiserJournalpostUgyldigAntallJournalposter, IdentifiserJournalpostObjektIkkeFunnet, IdentifiserJournalpostUgyldingInput {
                return null;
            }

            @Override
            public HentTilgjengeligJournalpostListeResponse hentTilgjengeligJournalpostListe(HentTilgjengeligJournalpostListeRequest hentTilgjengeligJournalpostListeRequest) throws HentTilgjengeligJournalpostListeSikkerhetsbegrensning {
                return null;
            }
        };

    }
}
