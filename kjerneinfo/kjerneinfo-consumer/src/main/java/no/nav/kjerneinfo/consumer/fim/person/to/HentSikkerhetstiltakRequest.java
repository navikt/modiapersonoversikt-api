package no.nav.kjerneinfo.consumer.fim.person.to;


import no.nav.common.auth.SubjectHandler;

public class HentSikkerhetstiltakRequest {
	private String ident;

	public HentSikkerhetstiltakRequest(String ident) {
		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	public String generateRequestId() {
		String subjectHandlerIdent = SubjectHandler.getIdent().orElse("-");
		return subjectHandlerIdent + ident;
	}
}
