package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.behandlejournal.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.binding.FerdigstillDokumentopplastingFerdigstillDokumentopplastingjournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlejournal.binding.LagreVedleggPaaJournalpostLagreVedleggPaaJournalpostjournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.ArkiverUstrukturertKravRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.ArkiverUstrukturertKravResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.FerdigstillDokumentopplastingRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.LagreVedleggPaaJournalpostRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.LagreVedleggPaaJournalpostResponse;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BehandleJournalV2PortTypeMock {

    public static BehandleJournalV2 createBehandleJournalPortTypeMock() {
        return new BehandleJournalV2() {
            @Override
            public ArkiverUstrukturertKravResponse arkiverUstrukturertKrav(ArkiverUstrukturertKravRequest arkiverUstrukturertKravRequest) {
                return new ArkiverUstrukturertKravResponse();
            }

            @Override
            public LagreVedleggPaaJournalpostResponse lagreVedleggPaaJournalpost(LagreVedleggPaaJournalpostRequest lagreVedleggPaaJournalpostRequest)
                    throws LagreVedleggPaaJournalpostLagreVedleggPaaJournalpostjournalpostIkkeFunnet {
                return new LagreVedleggPaaJournalpostResponse();
            }

            @Override
            public void ferdigstillDokumentopplasting(FerdigstillDokumentopplastingRequest ferdigstillDokumentopplastingRequest)
                    throws FerdigstillDokumentopplastingFerdigstillDokumentopplastingjournalpostIkkeFunnet {
            }

            @Override
            public void ping() {
            }

            @Override
            public JournalfoerNotatResponse journalfoerNotat(JournalfoerNotatRequest journalfoerNotatRequest) {
                return new JournalfoerNotatResponse();
            }

            @Override
            public JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelse(JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest) {
                return new JournalfoerUtgaaendeHenvendelseResponse();
            }

            @Override
            public JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelse(JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest) {
                return new JournalfoerInngaaendeHenvendelseResponse();
            }
        };
    }
}
