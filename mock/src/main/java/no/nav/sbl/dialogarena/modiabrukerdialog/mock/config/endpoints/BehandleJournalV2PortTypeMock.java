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
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXB;
import java.io.StringWriter;

import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class BehandleJournalV2PortTypeMock {

    private static final Logger logger = getLogger(BehandleJournalV2PortTypeMock.class);
    private static int journalpostId = 0;

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
                loggJournalforing("Journalført notat", journalfoerNotatRequest);

                JournalfoerNotatResponse journalfoerNotatResponse = new JournalfoerNotatResponse();
                journalfoerNotatResponse.setJournalpostId(valueOf(journalpostId++));
                return journalfoerNotatResponse;
            }

            @Override
            public JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelse(JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest) {
                loggJournalforing("Journalført utgående henvendelse", journalfoerUtgaaendeHenvendelseRequest);

                JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponse = new JournalfoerUtgaaendeHenvendelseResponse();
                journalfoerUtgaaendeHenvendelseResponse.setJournalpostId(valueOf(journalpostId++));
                return journalfoerUtgaaendeHenvendelseResponse;
            }

            @Override
            public JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelse(JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest) {
                loggJournalforing("Journalført inngående henvendelse", journalfoerInngaaendeHenvendelseRequest);

                JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponse = new JournalfoerInngaaendeHenvendelseResponse();
                journalfoerInngaaendeHenvendelseResponse.setJournalpostId(valueOf(journalpostId++));
                return journalfoerInngaaendeHenvendelseResponse;
            }
        };
    }

    private static void loggJournalforing(String overskrift, Object request) {
        StringWriter tekst = new StringWriter();
        tekst.append("\n========================================\n")
                .append(overskrift.toUpperCase())
                .append(":\n========================================\n");
        JAXB.marshal(request, tekst);
        logger.info(tekst.toString());
    }

}
