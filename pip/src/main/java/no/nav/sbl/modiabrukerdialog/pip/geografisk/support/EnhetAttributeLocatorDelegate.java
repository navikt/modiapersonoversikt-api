package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import java.util.Set;

/**
 * Delegate for retrieving enhet information.
 */
public interface EnhetAttributeLocatorDelegate {

	/**
	 * Henter ID verdier av alle enheter som er i samme fylke som lokal enhet til ansatt.
	 *
	 * @param ansattId
	 * @return Set av ID verdier
	 */
	Set<String> getFylkesenheterForAnsatt(String ansattId);

	/**
	 * Henter ID verdier til lokale enhetene for en ansatt.
	 *
	 * @param ansattId
	 * @return Set av AttributeValue med enhet ID.
	 */
	Set<String> getLokalEnheterForAnsatt(String ansattId);
}
