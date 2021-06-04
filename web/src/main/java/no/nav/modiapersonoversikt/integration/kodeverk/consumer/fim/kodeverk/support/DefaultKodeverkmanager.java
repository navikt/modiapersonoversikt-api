package no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.support;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.exceptions.TjenesteFeilException;
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.informasjon.Kode;
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.meldinger.HentKodeverkResponse;
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.utils.KodeverkMapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultKodeverkmanager implements KodeverkmanagerBi, Serializable, ApplicationListener<ContextRefreshedEvent> {

	public static final String KODEVERKSREF_VERGEMAL_FYLKESMANSSEMBETER = "Vergemål_Fylkesmannsembeter";
	public static final String KODEVERKSREF_VERGEMAL_MANDATTYPE= "Vergemål_Mandattype";
	public static final String KODEVERKSREF_VERGEMAL_SAKSTYPE = "Vergemål_Sakstype";
	public static final String KODEVERKSREF_VERGEMAL_VERGETYPE = "Vergemål_Vergetype";

	KodeverkMap kodeverkMap = new KodeverkMap();

	private KodeverkPortType kodeverkPortType;
	private KodeverkMapper mapper;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private long initialDelay;
	private long refreshDelay;

	public DefaultKodeverkmanager(KodeverkPortType kodeverkPortType) {
		this.kodeverkPortType = kodeverkPortType;
		mapper = KodeverkMapper.getInstance();
		initialDelay = 3;
		refreshDelay = 60 * 60 * 24;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if (this.initialDelay >= 0 && this.refreshDelay > 0) {
			ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
			service.scheduleAtFixedRate(this.loadDataFromKodeverk(), this.initialDelay, this.refreshDelay, TimeUnit.SECONDS);
		}
	}

	@Override
	public String getBeskrivelseForKode(String koderef, String kodeverksref, String spraak) {
		return kodeverkMap.getBeskrivelse(koderef, kodeverksref, spraak);
	}

	@Override
	public List<Kodeverdi> getKodeverkList(String kodeverksref, String spraak) {
		List<Kodeverdi> kodeverdiList = new LinkedList<>();
		if (kodeverkMap != null && kodeverkMap.get(kodeverksref) != null && kodeverkMap.get(kodeverksref).getKode() != null) {
			for (Kode kode : kodeverkMap.get(kodeverksref).getKode().values()) {
				Kodeverdi kodeverdi = new Kodeverdi(kode.getNavn(), kode.getTermForSpraak(spraak).getNavn());
				kodeverdi.setGyldig(kode.erGyldig());
				kodeverdi.setBeskrivelse(kode.getTermForSpraak(spraak).getNavn());
				kodeverdiList.add(kodeverdi);
			}
		}
		return kodeverdiList;
	}

	private Runnable loadDataFromKodeverk() {
		return () -> {
            logger.info("Starter lasting av kodeverk.");
            oppdaterKodeverk("Postnummer");
            oppdaterKodeverk("Valutaer");
            oppdaterKodeverk("Retningsnumre");
            oppdaterKodeverk("Kjønnstyper");
            oppdaterKodeverk("Sivilstander");
            oppdaterKodeverk("Personstatuser");
            oppdaterKodeverk("Landkoder");
			oppdaterKodeverk("Diskresjonskoder");
            oppdaterKodeverk("TilrettelagtKommunikasjon");
            oppdaterKodeverk("Språk");
            oppdaterKodeverk(KODEVERKSREF_VERGEMAL_FYLKESMANSSEMBETER);
            oppdaterKodeverk(KODEVERKSREF_VERGEMAL_MANDATTYPE);
            oppdaterKodeverk(KODEVERKSREF_VERGEMAL_SAKSTYPE);
            oppdaterKodeverk(KODEVERKSREF_VERGEMAL_VERGETYPE);
            logger.info("Kodeverkscache oppdatert.");
        };
	}

	private void oppdaterKodeverk(String kodeverksref) {

		XMLHentKodeverkRequest request = new XMLHentKodeverkRequest();
		request.setNavn(kodeverksref);
		try {
			XMLHentKodeverkResponse kodeverk = kodeverkPortType.hentKodeverk(request);
			HentKodeverkResponse response =  mapper.map(kodeverk);
			kodeverkMap.put(kodeverksref, response.getKodeverk());
			logger.info("Lagt til " + kodeverksref + " i kodeverkMap");
		} catch (TjenesteFeilException e) {
			logger.info("Tjenesten hentKodeverk returnerte en feil:" + e.getCause());
		} catch (Exception e) {
			logger.info("Kall mot tjenesten hentKodeverk feilet:" + e.getCause());
		}
	}

	public String getTelefonLand(String landkode) {
		return getBeskrivelseForKode(landkode, "Retningsnumre", "nb");
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public void setRefreshDelay(long refreshDelay) {
		this.refreshDelay = refreshDelay;
	}
}
