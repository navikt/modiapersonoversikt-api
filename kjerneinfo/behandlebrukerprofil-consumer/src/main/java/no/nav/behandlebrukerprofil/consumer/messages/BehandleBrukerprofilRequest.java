package no.nav.behandlebrukerprofil.consumer.messages;

import no.nav.brukerprofil.domain.Bruker;

public class BehandleBrukerprofilRequest {

    private Bruker bruker;

	public BehandleBrukerprofilRequest() {
	}

    public BehandleBrukerprofilRequest(Bruker bruker) {
        this.bruker = bruker;
    }

    public Bruker getBruker() {
        return bruker;
    }
}
