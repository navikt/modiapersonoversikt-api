package no.nav.kjerneinfo.consumer.fim.person.vergemal;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSFylkesmannsembete;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSMandattyper;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSVergesakstyper;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSVergetyper;

import static no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager.*;

public class VergemalKodeverkService {

    private final KodeverkmanagerBi kodeverkManager;

    public VergemalKodeverkService(KodeverkmanagerBi kodeverkManager) {
        this.kodeverkManager = kodeverkManager;
    }

    public Kodeverdi getEmbete(WSFylkesmannsembete embete) {
        return new Kodeverdi(embete.getValue(), getBeskrivelseForKode(embete.getValue(), KODEVERKSREF_VERGEMAL_FYLKESMANSSEMBETER));
    }

    public Kodeverdi getMandattype(WSMandattyper wsMandattyper) {
        return new Kodeverdi(wsMandattyper.getValue(), getBeskrivelseForKode(wsMandattyper.getValue(), KODEVERKSREF_VERGEMAL_MANDATTYPE));
    }

    public Kodeverdi getVergesakstype(WSVergesakstyper wsVergesakstyper) {
        return new Kodeverdi(wsVergesakstyper.getValue(), getBeskrivelseForKode(wsVergesakstyper.getValue(), KODEVERKSREF_VERGEMAL_SAKSTYPE));
    }

    public Kodeverdi getVergetype(WSVergetyper wsVergetyper) {
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
