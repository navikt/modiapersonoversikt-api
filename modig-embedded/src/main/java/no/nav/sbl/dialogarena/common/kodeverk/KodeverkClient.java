package no.nav.sbl.dialogarena.common.kodeverk;

import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;

public interface KodeverkClient {

    XMLKodeverk hentKodeverk(String navn);
    String hentFoersteTermnavnForKode(String kodenavn, String kodeverknavn);
    String hentFoersteTermnavnForKode(String kodenavn, XMLEnkeltKodeverk xmlEnkeltKodeverk);

}
