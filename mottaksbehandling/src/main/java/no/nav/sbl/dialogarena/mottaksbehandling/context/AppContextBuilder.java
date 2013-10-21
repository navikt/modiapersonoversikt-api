package no.nav.sbl.dialogarena.mottaksbehandling.context;

import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKo;
import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKoJMS;
import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKoStub;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepo;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepoReell;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepoStub;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgavesystem;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.OppgavesystemGsak;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.OppgavesystemStub;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.SakIntegrasjon;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.SakSystemGsak;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.SakSystemPensjon;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.QueueConnectionFactory;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.OppgavesystemIntegrasjon.oppgaveBehandlingWSKlient;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.OppgavesystemIntegrasjon.oppgaveWSKlient;

public class AppContextBuilder {
	
	private HendelseKo hendelseKo;
    private Oppgavesystem oppgavesystem;
    private HenvendelseRepo repo;
    private SakSystemGsak saksystem;
    private SakSystemPensjon pensjonSaksystem;

    public static AppContext altStubbet() {
    	return new AppContextBuilder().medOppgavesystemStub().medHendelseKoStub().medRepoStub().build();
    }
    
    public static AppContext altReellt() {
    	return new AppContextBuilder()
                .medOppgavesystemReell()
                .medHendelseKoReell()
                .medRepoReell()
                .medSaksystemReell()
                .medPensjonSaksystemReell()
                .build();
    }

    public AppContextBuilder medHendelseKoStub() {
    	hendelseKo = new HendelseKoStub();
    	return this;
    }
    
    public AppContextBuilder medHendelseKoReell() {
    	String brokerUrl = System.getProperty("activemq.broker.url", "tcp://localhost:61616");
    	LoggerFactory.getLogger(AppContextBuilder.class).info("Bruker ActiveMQBruker: '" + brokerUrl + "'");
		QueueConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
        Destination inn = new ActiveMQQueue("besvareHenvendelse");
        Destination ut = new ActiveMQQueue("verdikjedestyring");
        hendelseKo = new HendelseKoJMS(cf, inn, ut);
    	return this;
    }
    
    public AppContextBuilder medRepoStub() {
    	repo = new HenvendelseRepoStub();
    	return this;
    }
    
    public AppContextBuilder medRepoReell() {
		try {
			InitialContext ic = new InitialContext();
			DataSource ds = (DataSource) ic.lookup("java:/jboss/datasources/MottaksbehandlingDS");
			repo = new HenvendelseRepoReell(ds);
			return this;
		} catch (Exception e) {
			throw new RuntimeException("Feil ved lasting av datasource", e);
		}
    }
    
    public AppContextBuilder medOppgavesystemStub() {
    	oppgavesystem = new OppgavesystemStub();
    	return this;
    }
    
    public AppContextBuilder medOppgavesystemReell() {
    	oppgavesystem = new OppgavesystemGsak(oppgaveWSKlient(), oppgaveBehandlingWSKlient());
    	return this;
    }

    public AppContextBuilder medSaksystemReell() {
        saksystem = new SakSystemGsak(SakIntegrasjon.sakWSKlient());
        return this;
    }

    public AppContextBuilder medPensjonSaksystemReell() {
        pensjonSaksystem = new SakSystemPensjon(SakIntegrasjon.pensjonSakWSKlient());
        return this;
    }
    
    public AppContext build() {
    	return new AppContext(hendelseKo, oppgavesystem, repo, saksystem, pensjonSaksystem);
    }

}
