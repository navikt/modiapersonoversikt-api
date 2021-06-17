package no.nav.modiapersonoversikt.legacy.api.service.kodeverk;

public interface StandardKodeverk {

    void lastInnNyeKodeverk();

    String getArkivtemaNavn(String arkivtemaKode);

    void initKodeverk();

}
