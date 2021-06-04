package no.nav.modiapersonoversikt.integration.personsok.consumer.fim.kodeverk;

/**
 * Manager for retrieving code decodes.
 */
public interface KodeverkManager {

    String getBeskrivelseForKode(String kodeValue, String kodeverkNavn, String spraak);
}
