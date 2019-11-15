package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.HentOverordnetEnhetListeEnhetIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.Enhetsstatus;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.Organisasjonsenhet;
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
            public FinnNAVKontorResponse finnNAVKontor(FinnNAVKontorRequest finnNAVKontorRequest) throws FinnNAVKontorUgyldigInput {
                final FinnNAVKontorResponse finnNAVKontorResponse = new FinnNAVKontorResponse();
                Organisasjonsenhet navKontor = new Organisasjonsenhet();
                navKontor.setEnhetId("1234");
                navKontor.setEnhetNavn("NAV Mockenhet");
                navKontor.setStatus(Enhetsstatus.AKTIV);
                navKontor.setOrganisasjonsnummer("4321");
                finnNAVKontorResponse.setNAVKontor(navKontor);
                return finnNAVKontorResponse;
            }

            @Override
            public HentEnhetBolkResponse hentEnhetBolk(HentEnhetBolkRequest hentEnhetBolkRequest) {
                final HentEnhetBolkResponse hentEnhetBolkResponse = new HentEnhetBolkResponse();
                hentEnhetBolkResponse.getEnhetListe().add(lagDetaljertEnhet(1234));
                return hentEnhetBolkResponse;
            }


            @Override
            public HentFullstendigEnhetListeResponse hentFullstendigEnhetListe(HentFullstendigEnhetListeRequest hentFullstendigEnhetListeRequest) {
                HentFullstendigEnhetListeResponse hentFullstendigEnhetListeResponse = new HentFullstendigEnhetListeResponse();
                hentFullstendigEnhetListeResponse.getEnhetListe().add(lagDetaljertEnhet(1234));
                return hentFullstendigEnhetListeResponse;
            }

            @Override
            public void ping() {

            }

            @Override
            public HentOverordnetEnhetListeResponse hentOverordnetEnhetListe(HentOverordnetEnhetListeRequest hentOverordnetEnhetListeRequest) throws HentOverordnetEnhetListeEnhetIkkeFunnet {
                HentOverordnetEnhetListeResponse hentOverordnetEnhetListeResponse = new HentOverordnetEnhetListeResponse();
                hentOverordnetEnhetListeResponse.getOverordnetEnhetListe().add(lagDetaljertEnhet(2345));
                return hentOverordnetEnhetListeResponse;
            }

            private Organisasjonsenhet lagDetaljertEnhet(final int enhetId) {
                Organisasjonsenhet organisasjonsenhet = new Organisasjonsenhet();
                String enhet = StringUtils.leftPad(String.valueOf(enhetId), 4, '0');
                organisasjonsenhet.setEnhetId(enhet);
                organisasjonsenhet.setEnhetNavn("NAV Mockbrukers Enhet " + enhet);
                organisasjonsenhet.setStatus(Enhetsstatus.AKTIV);
                return organisasjonsenhet;
            }
        };
    }
}
