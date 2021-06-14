package no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;

import java.util.List;

public interface KodeverkmanagerBi {

    /**
     * Returnerer termen for en @koderef gitt @kodeverksref og @spraak
     * 
     * @param koderef
     * @param kodeverksref
     * @param spraak
     * @return
     * @throws HentKodeverkKodeverkIkkeFunnet
     */
    String getBeskrivelseForKode(String koderef, String kodeverksref, String spraak) throws HentKodeverkKodeverkIkkeFunnet;

    /**
     * Returnerer listen med kodeverdier for et @kodeverksref og @spraak
     * 
     * @param kodeverksref
     * @param spraak
     * @return
     * @throws HentKodeverkKodeverkIkkeFunnet
     */
    List<Kodeverdi> getKodeverkList(String kodeverksref, String spraak) throws HentKodeverkKodeverkIkkeFunnet;

	/**
	 * Returnerer land tilhørende retningsnummer fra @landkode
	 * @param landkode
	 * @return
	 */
	String getTelefonLand(String landkode);
}
