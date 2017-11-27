package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkResponse;
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
            public HentKontaktinformasjonForEnhetBolkResponse hentKontaktinformasjonForEnhetBolk(HentKontaktinformasjonForEnhetBolkRequest hentKontaktinformasjonForEnhetBolkRequest) throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
                HentKontaktinformasjonForEnhetBolkResponse hentKontaktinformasjonForEnhetBolkResponse = new HentKontaktinformasjonForEnhetBolkResponse();
                List<Organisasjonsenhet> enhetListe = hentKontaktinformasjonForEnhetBolkResponse.getEnhetListe();

                Organisasjonsenhet enhet = lagMockEnhet();
                enhetListe.add(enhet);

                return hentKontaktinformasjonForEnhetBolkResponse;
            }

            private Organisasjonsenhet lagMockEnhet() {
                Enhetstyper enhetstyper = new Enhetstyper();
                enhetstyper.setValue("enhetstypeverdi");
                enhetstyper.setKodeRef("enhetstypekode");

                Organisasjonsenhet enhet = new Organisasjonsenhet();
                enhet.setEnhetId("7080");
                enhet.setEnhetNavn("Nav Sør-Varanger");
                enhet.setKontaktinformasjon(getMockEnhetKontaktInformasjon());
                enhet.setOrganisasjonsnummer("123465");
                enhet.setStatus(Enhetsstatus.AKTIV);
                enhet.setType(enhetstyper);
                return enhet;
            }

            private KontaktinformasjonForOrganisasjonsenhet getMockEnhetKontaktInformasjon() {
                KontaktinformasjonForOrganisasjonsenhet kontaktinformasjonForOrganisasjonsenhet = new KontaktinformasjonForOrganisasjonsenhet();
                kontaktinformasjonForOrganisasjonsenhet.setTelefonnummer("12345678");
                kontaktinformasjonForOrganisasjonsenhet.setFaksnummer("87654321");
                kontaktinformasjonForOrganisasjonsenhet.setSpesielleOpplysninger("Mocket spesialopplysning");
                kontaktinformasjonForOrganisasjonsenhet.setBesoeksadresse(createMockGateAdresse());
                kontaktinformasjonForOrganisasjonsenhet.setPostadresse(createMockStrukturertAdresse());
                kontaktinformasjonForOrganisasjonsenhet.getPublikumsmottakListe().addAll(Arrays.asList(mockPublikumsmottak(), mockPublikumsmottak()));

                return kontaktinformasjonForOrganisasjonsenhet;
            }

            private Publikumsmottak mockPublikumsmottak() {
                Publikumsmottak publikumsmottak = new Publikumsmottak();
                publikumsmottak.setBesoeksadresse(createMockGateAdresse());
                publikumsmottak.setAapningstider(mockApningstider());
                return publikumsmottak;
            }

            private StrukturertAdresse createMockStrukturertAdresse() {
                return createMockGateAdresse();
            }

            private Gateadresse createMockGateAdresse() {
                Postnummer postnummer = new Postnummer();
                postnummer.setTermnavn("Andeby");
                postnummer.setValue("9999");

                Gateadresse gateadresse = new Gateadresse();
                gateadresse.setGatenavn("Andegata");
                gateadresse.setHusbokstav("A");
                gateadresse.setHusnummer("4");
                gateadresse.setPoststed(postnummer);

                return gateadresse;
            }

            private Aapningstider mockApningstider() {
                return new Aapningstider();
            }
        };
    }
}
