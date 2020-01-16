package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;


import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.WSHentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.WSHentKontaktinformasjonForEnhetBolkResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OrganisasjonEnhetKontaktinformasjonV1Mock {

    @Bean
    public static OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() {
        return new OrganisasjonEnhetKontaktinformasjonV1() {
            @Override
            public void ping() {
            }

            @Override
            public WSHentKontaktinformasjonForEnhetBolkResponse hentKontaktinformasjonForEnhetBolk(WSHentKontaktinformasjonForEnhetBolkRequest hentKontaktinformasjonForEnhetBolkRequest) throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
                WSHentKontaktinformasjonForEnhetBolkResponse hentKontaktinformasjonForEnhetBolkResponse = new WSHentKontaktinformasjonForEnhetBolkResponse();
                List<WSOrganisasjonsenhet> enhetListe = hentKontaktinformasjonForEnhetBolkResponse.getEnhetListe();

                WSOrganisasjonsenhet enhet = lagMockEnhet();
                enhetListe.add(enhet);

                return hentKontaktinformasjonForEnhetBolkResponse;
            }

            private WSOrganisasjonsenhet lagMockEnhet() {
                WSEnhetstyper enhetstyper = new WSEnhetstyper();
                enhetstyper.setValue("enhetstypeverdi");
                enhetstyper.setKodeRef("enhetstypekode");

                WSOrganisasjonsenhet enhet = new WSOrganisasjonsenhet();
                enhet.setEnhetId("7080");
                enhet.setEnhetNavn("Nav Sør-Varanger");
                enhet.setKontaktinformasjon(getMockEnhetKontaktInformasjon());
                enhet.setOrganisasjonsnummer("123465");
                enhet.setStatus(WSEnhetsstatus.AKTIV);
                enhet.setType(enhetstyper);
                return enhet;
            }

            private WSKontaktinformasjonForOrganisasjonsenhet getMockEnhetKontaktInformasjon() {
                WSKontaktinformasjonForOrganisasjonsenhet kontaktinformasjonForOrganisasjonsenhet = new WSKontaktinformasjonForOrganisasjonsenhet();
                kontaktinformasjonForOrganisasjonsenhet.setTelefonnummer("12345678");
                kontaktinformasjonForOrganisasjonsenhet.setFaksnummer("87654321");
                kontaktinformasjonForOrganisasjonsenhet.setSpesielleOpplysninger("Mocket spesialopplysning");
                kontaktinformasjonForOrganisasjonsenhet.setBesoeksadresse(createMockGateAdresse());
                kontaktinformasjonForOrganisasjonsenhet.setPostadresse(createMockStrukturertAdresse());
                kontaktinformasjonForOrganisasjonsenhet.getPublikumsmottakListe().addAll(Arrays.asList(mockPublikumsmottak(), mockPublikumsmottak()));

                return kontaktinformasjonForOrganisasjonsenhet;
            }

            private WSPublikumsmottak mockPublikumsmottak() {
                WSPublikumsmottak publikumsmottak = new WSPublikumsmottak();
                publikumsmottak.setBesoeksadresse(createMockGateAdresse());
                publikumsmottak.setAapningstider(mockApningstider());
                return publikumsmottak;
            }

            private WSStrukturertAdresse createMockStrukturertAdresse() {
                return createMockGateAdresse();
            }

            private WSGateadresse createMockGateAdresse() {
                WSPostnummer postnummer = new WSPostnummer();
                postnummer.setTermnavn("Andeby");
                postnummer.setValue("9999");

                WSGateadresse gateadresse = new WSGateadresse();
                gateadresse.setGatenavn("Andegata");
                gateadresse.setHusbokstav("A");
                gateadresse.setHusnummer("4");
                gateadresse.setPoststed(postnummer);

                return gateadresse;
            }

            private WSAapningstider mockApningstider() {
                return new WSAapningstider();
            }
        };
    }
}
