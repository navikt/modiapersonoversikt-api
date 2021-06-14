package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Fylkesmannsembete;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Mandattyper;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Vergesakstyper;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Vergetyper;

import static no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager.*;

public class VergemalKodeverkService {

    private final KodeverkmanagerBi kodeverkManager;

    public VergemalKodeverkService(KodeverkmanagerBi kodeverkManager) {
        this.kodeverkManager = kodeverkManager;
    }

    public Kodeverdi getEmbete(Fylkesmannsembete embete) {
        return new Kodeverdi(embete.getValue(), getBeskrivelseForKode(embete.getValue(), KODEVERKSREF_VERGEMAL_FYLKESMANSSEMBETER));
    }

    public Kodeverdi getMandattype(Mandattyper wsMandattyper) {
        return new Kodeverdi(wsMandattyper.getValue(), getBeskrivelseForKode(wsMandattyper.getValue(), KODEVERKSREF_VERGEMAL_MANDATTYPE));
    }

    public Kodeverdi getVergesakstype(Vergesakstyper wsVergesakstyper) {
        return new Kodeverdi(wsVergesakstyper.getValue(), getBeskrivelseForKode(wsVergesakstyper.getValue(), KODEVERKSREF_VERGEMAL_SAKSTYPE));
    }

    public Kodeverdi getVergetype(Vergetyper wsVergetyper) {
        return new Kodeverdi(wsVergetyper.getValue(), getBeskrivelseForKode(wsVergetyper.getValue(), KODEVERKSREF_VERGEMAL_VERGETYPE));
    }

    private String getBeskrivelseForKode(String kode, String kodeverksref) {
        try {
            return kodeverkManager.getBeskrivelseForKode(kode, kodeverksref, "nb");
        } catch (HentKodeverkKodeverkIkkeFunnet hentKodeverkKodeverkIkkeFunnet) {
            return "";
        }
    }

}
