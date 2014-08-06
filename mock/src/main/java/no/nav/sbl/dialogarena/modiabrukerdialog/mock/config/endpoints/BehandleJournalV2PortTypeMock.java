package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.FerdigstillDokumentopplastingFerdigstillDokumentopplastingjournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.LagreVedleggPaaJournalpostLagreVedleggPaaJournalpostjournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.UstrukturertInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.Journalpost;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.DokumentinfoRelasjon;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class BehandleJournalV2PortTypeMock {

    private static final Logger logger = getLogger(BehandleJournalV2PortTypeMock.class);
    private static int journalpostId = 0;

    @Bean
    public BehandleJournalV2 behandleJournalV2Mock() {
        return createBehandleJournalPortTypeMock();
    }

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

                byte[] innhold = hentJounalfortNotatInnhold(journalfoerNotatRequest);
                lagreTilDisk(innhold, "/var/tmp/notat.pdf");

                JournalfoerNotatResponse journalfoerNotatResponse = new JournalfoerNotatResponse();
                journalfoerNotatResponse.setJournalpostId(valueOf(journalpostId++));
                return journalfoerNotatResponse;
            }

            @Override
            public JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelse(JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest) {
                loggJournalforing("Journalført utgående henvendelse", journalfoerUtgaaendeHenvendelseRequest);

                byte[] innhold = hentJounalfortSvarInnhold(journalfoerUtgaaendeHenvendelseRequest);
                lagreTilDisk(innhold, "/var/tmp/svar.pdf");

                JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponse = new JournalfoerUtgaaendeHenvendelseResponse();
                journalfoerUtgaaendeHenvendelseResponse.setJournalpostId(valueOf(journalpostId++));
                return journalfoerUtgaaendeHenvendelseResponse;
            }

            @Override
            public JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelse(JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest) {
                loggJournalforing("Journalført inngående henvendelse", journalfoerInngaaendeHenvendelseRequest);

                byte[] innhold = hentJounalfortSporsmaalInnhold(journalfoerInngaaendeHenvendelseRequest);
                lagreTilDisk(innhold, "/var/tmp/sporsmaal.pdf");

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

    private static void lagreTilDisk(byte[] bytes, String pathname) {

        try {
            File file = createFile(pathname);
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            logger.debug("Feil ved opprettelse av pdf fil for lagrring på disk", e);
        }

        logger.info("Pdf lagret til disk: " + pathname);
    }

    private static File createFile(String pathname) throws IOException {
        File file = new File(pathname);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private static byte[] hentJounalfortSporsmaalInnhold(JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest) {
        Journalpost journalpost = journalfoerInngaaendeHenvendelseRequest.getJournalpost();
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.DokumentinfoRelasjon dokumentinfoRelasjon = journalpost.getDokumentinfoRelasjon().get(0);
        UstrukturertInnhold ustrukturertInnhold = (UstrukturertInnhold) dokumentinfoRelasjon.getJournalfoertDokument().getBeskriverInnhold().get(0);
        return ustrukturertInnhold.getInnhold();
    }

    private static byte[] hentJounalfortSvarInnhold(JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest) {
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost journalpost = journalfoerUtgaaendeHenvendelseRequest.getJournalpost();
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon dokumentinfoRelasjon = journalpost.getDokumentinfoRelasjon().get(0);
        UstrukturertInnhold ustrukturertInnhold = (UstrukturertInnhold) dokumentinfoRelasjon.getJournalfoertDokument().getBeskriverInnhold().get(0);
        return ustrukturertInnhold.getInnhold();
    }


    private static byte[] hentJounalfortNotatInnhold(JournalfoerNotatRequest journalfoerNotatRequest) {
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost journalpost = journalfoerNotatRequest.getJournalpost();
        DokumentinfoRelasjon dokumentinfoRelasjon = journalpost.getDokumentinfoRelasjon().get(0);
        UstrukturertInnhold ustrukturertInnhold = (UstrukturertInnhold) dokumentinfoRelasjon.getJournalfoertDokument().getBeskriverInnhold().get(0);
        return ustrukturertInnhold.getInnhold();
    }

}
