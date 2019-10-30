package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.common.auth.SubjectHandler;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLOppgaveOpprettetInformasjon;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock.HENVENDELSER;

@Configuration
public class BehandleHenvendelsePortTypeMock {

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        return createBehandleHenvendelsePortTypeMock();
    }

    public static BehandleHenvendelsePortType createBehandleHenvendelsePortTypeMock() {
        return new BehandleHenvendelsePortType() {

            @Override
            public void oppdaterJournalfortInformasjon(String behandlingId, XMLJournalfortInformasjon journalfortInformasjon) {
                hentHenvendelse(behandlingId).setJournalfortInformasjon(journalfortInformasjon);
            }

            @Override
            public void oppdaterTemagruppe(String behandlingsId, String temagruppe) {
                hentHenvendelse(behandlingsId).setGjeldendeTemagruppe(temagruppe);
            }

            @Override
            public void knyttBehandlingskjedeTilSak(String behandlingskjedeId, String saksId, String temakode, String journalforendeEnhet) {
                String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Ukjent saksbehandler"));
                for (XMLHenvendelse xmlHenvendelse : hentBehandlingskjede(behandlingskjedeId)) {
                    xmlHenvendelse.setJournalfortInformasjon(new XMLJournalfortInformasjon()
                            .withJournalfortDato(DateTime.now())
                            .withJournalforerNavIdent(ident)
                            .withJournalfortSaksId(saksId)
                            .withJournalfortTema(temakode));
                }
            }

            @Override
            public void oppdaterOppgaveOpprettetInformasjon(XMLOppgaveOpprettetInformasjon oppdaterOppgaveOpprettetInformasjon) {
            }

            @Override
            public void ferdigstillUtenSvar(String behandlingsId, String enhetId) {
                hentHenvendelse(behandlingsId).setFerdigstiltUtenSvar(true);
            }

            @Override
            public void oppdaterTilKassering(List<String> behandlingsIdListe) {
                String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Ukjent saksbehandler"));
                for (String id : behandlingsIdListe) {
                    XMLHenvendelse xmlHenvendelse = hentHenvendelse(id);
                    xmlHenvendelse.setMarkertSomFeilsendtAv(ident);
                }
            }

            @Override
            public void markerTraadForHasteKassering(List<String> behandlingsIdListe) {
                String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Ukjent saksbehandler"));
                for (String id : behandlingsIdListe) {
                    XMLHenvendelse xmlHenvendelse = hentHenvendelse(id);
                    xmlHenvendelse.setMarkertSomFeilsendtAv(ident);
                }
            }

            @Override
            public void knyttBehandlingskjedeTilTema(String s, String s1) {

            }

            @Override
            public void settOversendtDokmot(String s, XMLGregorianCalendar xmlGregorianCalendar) {
            }

            @Override
            public void oppdaterHenvendelsesarkivInformasjon(String behandlingId, String arkivpostId) {
            }

            @Override
            public void oppdaterKontorsperre(String enhet, List<String> behandlingsIdListe) {
                for (String id : behandlingsIdListe) {
                    XMLHenvendelse xmlHenvendelse = hentHenvendelse(id);
                    xmlHenvendelse.setKontorsperreEnhet(enhet);
                }
            }

            @Override
            public void ping() {
            }
        };

    }

    private static XMLHenvendelse hentHenvendelse(String behandlingsId) {
        for (XMLHenvendelse henvendelse : HENVENDELSER) {
            if (behandlingsId.equals(henvendelse.getBehandlingsId())) {
                return henvendelse;
            }
        }
        return new XMLHenvendelse();
    }

    private static List<XMLHenvendelse> hentBehandlingskjede(final String behandlingskjedeId) {
        return HENVENDELSER.stream()
                .filter(henvendelse -> behandlingskjedeId.equals(henvendelse.getBehandlingskjedeId()))
                .collect(toList());
    }
}
