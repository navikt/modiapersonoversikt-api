package no.nav.kjerneinfo.consumer.fim.person.vergemal;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Periode;
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge;
import no.nav.kjerneinfo.domain.person.Personnavn;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.tjeneste.virksomhet.person.v3.HentVergePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.HentVergeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentVergeRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentVergeResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VergemalService {

    public static final String TPS_VERGES_FNR_MANGLENDE_DATA = "00000000000";

    private final PersonV3 personV3;
    private final PersonKjerneinfoServiceBi personService;
    private final VergemalKodeverkService vergemalKodeverkService;

    public VergemalService(PersonV3 personV3, PersonKjerneinfoServiceBi personService, KodeverkmanagerBi kodeverkManager) {
        this.personV3 = personV3;
        this.personService = personService;
        this.vergemalKodeverkService = new VergemalKodeverkService(kodeverkManager);
    }

    public List<Verge> hentVergemal(String fodselsnummer) {
        WSHentVergeResponse wsHentVergeResponse = hentVergemalFraTPS(fodselsnummer);
        return wsHentVergeResponse.getVergeListe().stream()
                .map(verge -> lagVergeDomeneObjekt(verge))
                .collect(Collectors.toList());
    }

    private Verge lagVergeDomeneObjekt(WSVerge verge) {
        String ident = getIdentFromVerge(verge);
        Personnavn navn = hentPersonnNavn(ident);
        return new Verge()
                .withSakstype(getVergesakstype(verge).orElse(null))
                .withMandattype(getMandatype(verge).orElse(null))
                .withMandattekst(verge.getMandatTekst())
                .withVirkningsperiode(map(verge.getVirkningsperiode()))
                .withEmbete(getEmbete(verge).orElse(null))
                .withVergetype(getVergetype(verge).orElse(null))
                .withPersonnavn(navn)
                .withIdent(ident);
    }

    private WSHentVergeResponse hentVergemalFraTPS(String fodselsnummer) {
        WSHentVergeRequest request = lagRequest(fodselsnummer);
        try {
            return personV3.hentVerge(request);
        } catch (HentVergeSikkerhetsbegrensning | HentVergePersonIkkeFunnet e) {
            throw new RuntimeException(e);
        }
    }

    private WSHentVergeRequest lagRequest(String fodselsnummer) {
        return new WSHentVergeRequest().withAktoer(new WSPersonIdent().withIdent(new WSNorskIdent().withIdent(fodselsnummer)));
    }

    private HentKjerneinformasjonRequest kjerneInfoRequestMedBegrunnet(String ident) {
        HentKjerneinformasjonRequest request = new HentKjerneinformasjonRequest(ident);
        request.setBegrunnet(true);
        return request;
    }

    private String getIdentFromVerge(WSVerge verge) {
        WSAktoer vergeAktoer = verge.getVerge();
        if (vergeAktoer instanceof WSPersonIdent) {
            String ident =  ((WSPersonIdent) vergeAktoer).getIdent().getIdent();
            return ident.equals(TPS_VERGES_FNR_MANGLENDE_DATA) ? null : ident;
        } else {
            throw new RuntimeException("Ident for vegemal er av ukjent type");
        }
    }

    private Personnavn hentPersonnNavn(String ident) {
        if (ident == null) {
            return null;
        }
        try {
            HentKjerneinformasjonResponse response = personService.hentKjerneinformasjon(kjerneInfoRequestMedBegrunnet(ident));
            return response.getPerson().getPersonfakta().getPersonnavn();
        } catch (Exception e) {
            return null;
        }
    }

    private Optional<Kodeverdi> getVergetype(WSVerge verge) {
        return Optional.ofNullable(verge.getVergetype()).map(vergemalKodeverkService::getVergetype);
    }

    private Optional<Kodeverdi> getEmbete(WSVerge verge) {
        return Optional.ofNullable(verge.getEmbete()).map(vergemalKodeverkService::getEmbete);
    }

    private Periode map(WSPeriode virkningsperiode) {
        return new Periode(virkningsperiode.getFom(), virkningsperiode.getTom());
    }

    private Optional<Kodeverdi> getMandatype(WSVerge verge) {
        return Optional.ofNullable(verge.getMandattype()).map(vergemalKodeverkService::getMandattype);
    }

    private Optional<Kodeverdi> getVergesakstype(WSVerge verge) {
        return Optional.ofNullable(verge.getVergesakstype()).map(vergemalKodeverkService::getVergesakstype);
    }
}
