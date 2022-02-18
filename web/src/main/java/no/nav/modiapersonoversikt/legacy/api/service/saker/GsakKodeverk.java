package no.nav.modiapersonoversikt.legacy.api.service.saker;


import java.io.Serializable;
import java.util.Map;

public interface GsakKodeverk extends Serializable {

    Map<String, String> hentFagsystemMapping();

}
