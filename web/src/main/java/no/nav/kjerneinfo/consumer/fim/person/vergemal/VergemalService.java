package no.nav.kjerneinfo.consumer.fim.person.vergemal;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Periode;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentVergePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentVergeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Verge;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentVergeRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentVergeResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VergemalService {

    public static final String TPS_VERGES_FNR_MANGLENDE_DATA = "00000000000";

    private final PdlOppslagService pdl;
    private final PersonV3 personV3;
    private final VergemalKodeverkService vergemalKodeverkService;

    public VergemalService(PersonV3 personV3, PdlOppslagService pdl, KodeverkmanagerBi kodeverkManager) {
        this.personV3 = personV3;
        this.pdl = pdl;
        this.vergemalKodeverkService = new VergemalKodeverkService(kodeverkManager);
    }

    public List<no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge> hentVergemal(String fodselsnummer) {
        HentVergeResponse wsHentVergeResponse = hentVergemalFraTPS(fodselsnummer);
        List<String> vergeIdenter = wsHentVergeResponse.getVergeListe()
                .stream()
                .map(this::getIdentFromVerge)
                .collect(Collectors.toList());
        Map<String, HentNavnBolk.Navn> vergeNavn = pdl.hentNavnBolk(vergeIdenter);

        return wsHentVergeResponse.getVergeListe().stream()
                .map(verge -> lagVergeDomeneObjekt(verge, vergeNavn))
                .collect(Collectors.toList());
    }

    private no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge lagVergeDomeneObjekt(Verge verge, Map<String, HentNavnBolk.Navn> vergeNavn) {
        String ident = getIdentFromVerge(verge);
        HentNavnBolk.Navn navn = vergeNavn.get(getIdentFromVerge(verge));

        return new no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge()
                .withSakstype(getVergesakstype(verge).orElse(null))
                .withMandattype(getMandatype(verge).orElse(null))
                .withMandattekst(verge.getMandatTekst())
                .withVirkningsperiode(map(verge.getVirkningsperiode()))
                .withEmbete(getEmbete(verge).orElse(null))
                .withVergetype(getVergetype(verge).orElse(null))
                .withPersonnavn(navn)
                .withIdent(ident);
    }

    private HentVergeResponse hentVergemalFraTPS(String fodselsnummer) {
        HentVergeRequest request = lagRequest(fodselsnummer);
        try {
            return personV3.hentVerge(request);
        } catch (HentVergeSikkerhetsbegrensning | HentVergePersonIkkeFunnet e) {
            throw new RuntimeException(e);
        }
    }

    private HentVergeRequest lagRequest(String fodselsnummer) {
        return new HentVergeRequest().withAktoer(new PersonIdent().withIdent(new NorskIdent().withIdent(fodselsnummer)));
    }

    private String getIdentFromVerge(Verge verge) {
        Aktoer vergeAktoer = verge.getVerge();
        if (vergeAktoer instanceof PersonIdent) {
            String ident =  ((PersonIdent) vergeAktoer).getIdent().getIdent();
            return ident.equals(TPS_VERGES_FNR_MANGLENDE_DATA) ? null : ident;
        } else {
            throw new RuntimeException("Ident for vegemal er av ukjent type");
        }
    }

    private Optional<Kodeverdi> getVergetype(Verge verge) {
        return Optional.ofNullable(verge.getVergetype()).map(vergemalKodeverkService::getVergetype);
    }

    private Optional<Kodeverdi> getEmbete(Verge verge) {
        return Optional.ofNullable(verge.getEmbete()).map(vergemalKodeverkService::getEmbete);
    }

    private Periode map(no.nav.tjeneste.virksomhet.person.v3.informasjon.Periode virkningsperiode) {
        return new Periode(virkningsperiode.getFom(), virkningsperiode.getTom());
    }

    private Optional<Kodeverdi> getMandatype(Verge verge) {
        return Optional.ofNullable(verge.getMandattype()).map(vergemalKodeverkService::getMandattype);
    }

    private Optional<Kodeverdi> getVergesakstype(Verge verge) {
        return Optional.ofNullable(verge.getVergesakstype()).map(vergemalKodeverkService::getVergesakstype);
    }
}
