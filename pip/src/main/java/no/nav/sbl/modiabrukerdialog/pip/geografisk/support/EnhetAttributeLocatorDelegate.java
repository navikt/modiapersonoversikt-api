package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;

import java.util.Set;

public interface EnhetAttributeLocatorDelegate {

	Set<String> getFylkesenheterForAnsatt(String ansattId);

	Set<String> getLokalEnheterForAnsatt(String ansattId);

	Set<Arbeidsfordeling> getArbeidsfordelingForValgtEnhet();
}
