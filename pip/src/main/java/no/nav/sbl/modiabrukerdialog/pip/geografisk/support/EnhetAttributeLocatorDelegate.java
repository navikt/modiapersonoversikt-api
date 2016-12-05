package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import java.util.Set;

public interface EnhetAttributeLocatorDelegate {

	Set<String> getFylkesenheterForAnsatt(String ansattId);

	Set<String> getLokalEnheterForAnsatt(String ansattId);

	Set<String> getArbeidsfordelingForValgtEnhet();
}
