package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLOppgaveOpprettetInformasjon;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
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
                for (XMLHenvendelse xmlHenvendelse : hentBehandlingskjede(behandlingskjedeId)) {
                    xmlHenvendelse.setJournalfortInformasjon(new XMLJournalfortInformasjon()
                            .withJournalfortDato(DateTime.now())
                            .withJournalforerNavIdent(getSubjectHandler().getUid())
                            .withJournalfortSaksId(saksId)
                            .withJournalfortTema(temakode));
                }
            }

            @Override
            public void oppdaterOppgaveOpprettetInformasjon(XMLOppgaveOpprettetInformasjon oppdaterOppgaveOpprettetInformasjon) {
            }

            @Override
            public void oppdaterTilKassering(List<String> behandlingsIdListe) {
                for (String id : behandlingsIdListe) {
                    XMLHenvendelse xmlHenvendelse = hentHenvendelse(id);
                    xmlHenvendelse.setMarkertSomFeilsendtAv(getSubjectHandler().getUid());
                }
            }

            @Override
            public void knyttBehandlingskjedeTilTema(String s, String s1) {

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
        return on(HENVENDELSER).filter(new Predicate<XMLHenvendelse>() {
            @Override
            public boolean evaluate(XMLHenvendelse henvendelse) {
                return behandlingskjedeId.equals(henvendelse.getBehandlingskjedeId());
            }
        }).collect();
    }
}
