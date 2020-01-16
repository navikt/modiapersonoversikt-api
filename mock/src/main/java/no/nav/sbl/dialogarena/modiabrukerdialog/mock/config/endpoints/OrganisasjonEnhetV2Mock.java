package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;


import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.HentOverordnetEnhetListeEnhetIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSEnhetsstatus;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganisasjonEnhetV2Mock {

    @Bean
    public static OrganisasjonEnhetV2 organisasjonEnhetV2() {
        return new OrganisasjonEnhetV2() {
            @Override
            public WSFinnNAVKontorResponse finnNAVKontor(WSFinnNAVKontorRequest finnNAVKontorRequest) throws FinnNAVKontorUgyldigInput {
                final WSFinnNAVKontorResponse finnNAVKontorResponse = new WSFinnNAVKontorResponse();
                WSOrganisasjonsenhet navKontor = new WSOrganisasjonsenhet();
                navKontor.setEnhetId("1234");
                navKontor.setEnhetNavn("NAV Mockenhet");
                navKontor.setStatus(WSEnhetsstatus.AKTIV);
                navKontor.setOrganisasjonsnummer("4321");
                finnNAVKontorResponse.setNAVKontor(navKontor);
                return finnNAVKontorResponse;
            }

            @Override
            public WSHentEnhetBolkResponse hentEnhetBolk(WSHentEnhetBolkRequest hentEnhetBolkRequest) {
                final WSHentEnhetBolkResponse hentEnhetBolkResponse = new WSHentEnhetBolkResponse();
                hentEnhetBolkResponse.getEnhetListe().add(lagDetaljertEnhet(1234));
                return hentEnhetBolkResponse;
            }


            @Override
            public WSHentFullstendigEnhetListeResponse hentFullstendigEnhetListe(WSHentFullstendigEnhetListeRequest hentFullstendigEnhetListeRequest) {
                WSHentFullstendigEnhetListeResponse hentFullstendigEnhetListeResponse = new WSHentFullstendigEnhetListeResponse();
                hentFullstendigEnhetListeResponse.getEnhetListe().add(lagDetaljertEnhet(1234));
                return hentFullstendigEnhetListeResponse;
            }

            @Override
            public void ping() {

            }

            @Override
            public WSHentOverordnetEnhetListeResponse hentOverordnetEnhetListe(WSHentOverordnetEnhetListeRequest hentOverordnetEnhetListeRequest) throws HentOverordnetEnhetListeEnhetIkkeFunnet {
                WSHentOverordnetEnhetListeResponse hentOverordnetEnhetListeResponse = new WSHentOverordnetEnhetListeResponse();
                hentOverordnetEnhetListeResponse.getOverordnetEnhetListe().add(lagDetaljertEnhet(2345));
                return hentOverordnetEnhetListeResponse;
            }

            private WSOrganisasjonsenhet lagDetaljertEnhet(final int enhetId) {
                WSOrganisasjonsenhet organisasjonsenhet = new WSOrganisasjonsenhet();
                String enhet = StringUtils.leftPad(String.valueOf(enhetId), 4, '0');
                organisasjonsenhet.setEnhetId(enhet);
                organisasjonsenhet.setEnhetNavn("NAV Mockbrukers Enhet " + enhet);
                organisasjonsenhet.setStatus(WSEnhetsstatus.AKTIV);
                return organisasjonsenhet;
            }
        };
    }
}
