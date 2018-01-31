package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.*;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.Aapningstid;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.Aapningstider;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.KontaktinformasjonForOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.Organisasjonsenhet;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrganisasjonEnhetKontaktinformasjonMapper {

    public static OrganisasjonEnhetKontaktinformasjon map(Organisasjonsenhet organisasjonsenhetWS) {
        return new OrganisasjonEnhetKontaktinformasjon()
                .withEnhetId(organisasjonsenhetWS.getEnhetId())
                .withEnhetNavn(organisasjonsenhetWS.getEnhetNavn())
                .withKontaktinformasjon(map(organisasjonsenhetWS.getKontaktinformasjon()));
    }

    private static Kontaktinformasjon map(KontaktinformasjonForOrganisasjonsenhet kontaktinformasjonForOrganisasjonsenhetWS) {
        return new Kontaktinformasjon()
                .withPublikumsmottakliste(map(kontaktinformasjonForOrganisasjonsenhetWS.getPublikumsmottakListe()));
    }

    private static List<Publikumsmottak> map(List<no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.Publikumsmottak> publikumsmottakWS) {
        return publikumsmottakWS.stream()
                .map(OrganisasjonEnhetKontaktinformasjonMapper::map)
                .collect(Collectors.toList());
    }

    private static Publikumsmottak map(no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.Publikumsmottak publikumsmottakWS) {
        return new Publikumsmottak()
                .withApningstider(map(publikumsmottakWS.getAapningstider()))
                .withBesoeksadresse(map(publikumsmottakWS.getBesoeksadresse()));
    }

    private static Gateadresse map(no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.Gateadresse gateadresseWS) {
        return Optional.ofNullable(gateadresseWS).map(gateadresse -> new Gateadresse()
                .withGatenavn(gateadresse.getGatenavn())
                .withHusbokstav(gateadresse.getHusbokstav())
                .withHusnummer(gateadresse.getHusnummer())
                .withPoststed(Optional.ofNullable(gateadresse.getPoststed().getTermnavn()).orElse(""))
                .withPostnummer(Optional.ofNullable(gateadresse.getPoststed().getValue()).orElse("")))
                .orElse(null);
    }

    private static Apningstider map(Aapningstider aapningstiderWS) {
        List<Apningstid> apningstider = Stream.of(
                map(aapningstiderWS.getMandag(), Ukedag.MANDAG),
                map(aapningstiderWS.getTirsdag(), Ukedag.TIRSDAG),
                map(aapningstiderWS.getOnsdag(), Ukedag.ONSDAG),
                map(aapningstiderWS.getTorsdag(), Ukedag.TORSDAG),
                map(aapningstiderWS.getFredag(), Ukedag.FREDAG),
                map(aapningstiderWS.getLoerdag(), Ukedag.LORDAG),
                map(aapningstiderWS.getSoendag(), Ukedag.SONDAG))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new Apningstider().withApningstid(apningstider);
    }

    private static Apningstid map(Aapningstid apningtidWS, Ukedag ukedag) {
        return Optional.ofNullable(apningtidWS)
                .map(apningstid -> new Apningstid()
                        .withApentTil(map(apningtidWS.getAapentTil()))
                        .withApentFra(map(apningtidWS.getAapentFra()))
                        .withUkedag(ukedag))
                .orElse(null);
    }

    private static Klokkeslett map(XMLGregorianCalendar xmlGregorianCalendar) {
        return new Klokkeslett(xmlGregorianCalendar.getHour(), xmlGregorianCalendar.getMinute(), xmlGregorianCalendar.getSecond());
    }
}
