package no.nav.sbl.dialogarena.common.kodeverk;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.kodeverk.exception.KodeverkIkkeFunnetException;
import no.nav.sbl.dialogarena.common.kodeverk.exception.KodeverkTjenesteFeiletException;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverkselement;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.joda.time.DateTime.now;

public class DefaultKodeverkClient implements KodeverkClient {

    private KodeverkPortType kodeverkPortType;

    public DefaultKodeverkClient(KodeverkPortType kodeverkPortType) {
        this.kodeverkPortType = kodeverkPortType;
    }

    @Override
    public XMLKodeverk hentKodeverk(String navn) {
        XMLKodeverk kodeverk;
        try {
            XMLHentKodeverkRequest request = new XMLHentKodeverkRequest().withNavn(navn).withSpraak("nb");
            kodeverk = kodeverkPortType.hentKodeverk(request).getKodeverk();
            filtrerGyldigPeriode(kodeverk);
        } catch (HentKodeverkHentKodeverkKodeverkIkkeFunnet kodeverkIkkeFunnet) {
            throw new KodeverkIkkeFunnetException(navn, kodeverkIkkeFunnet);
        } catch (RuntimeException e) {
            throw new KodeverkTjenesteFeiletException(e);
        }
        return kodeverk;
    }

    private void filtrerGyldigPeriode(XMLKodeverk kodeverk) {
        if (kodeverk instanceof XMLEnkeltKodeverk) {
            XMLEnkeltKodeverk enkeltKodeverk = (XMLEnkeltKodeverk) kodeverk;
            filtrerGyldigeKoder(enkeltKodeverk);
            filtrerGyldigeTermer(enkeltKodeverk);
        }
    }

    private void filtrerGyldigeKoder(XMLEnkeltKodeverk enkeltKodeverk) {
        filtrerGyldigKodeverksElement(enkeltKodeverk.getKode());
    }

    private void filtrerGyldigeTermer(XMLEnkeltKodeverk enkeltKodeverk) {
        for (XMLKode kode : enkeltKodeverk.getKode()) {
            filtrerGyldigKodeverksElement(kode.getTerm());
        }
    }

    private <T extends XMLKodeverkselement> void filtrerGyldigKodeverksElement(Collection<T> liste) {
        Collection<T> gyldige = liste
                .stream()
                .filter(kodeverkselement -> kodeverkselement
                        .getGyldighetsperiode()
                        .stream()
                        .anyMatch(xmlPeriode -> now().isAfter(xmlPeriode.getFom()) && now().isBefore(xmlPeriode.getTom()))
                )
                .collect(toList());
        liste.clear();
        liste.addAll(gyldige);
    }

    @Override
    public String hentFoersteTermnavnForKode(String kodenavn, String kodeverknavn) {
        XMLEnkeltKodeverk xmlEnkeltKodeverk = (XMLEnkeltKodeverk) hentKodeverk(kodeverknavn);
        return hentFoersteTermnavnForKode(kodenavn, xmlEnkeltKodeverk);
    }

    @Override
    public String hentFoersteTermnavnForKode(String kodenavn, XMLEnkeltKodeverk xmlEnkeltKodeverk) {
        List<XMLKode> kodeListe = xmlEnkeltKodeverk.getKode();

        Optional<XMLKode> kode = kodeListe.stream()
                .filter(xmlKode -> kodenavn.equals(xmlKode.getNavn()))
                .findFirst();

        if (kode.isEmpty()) {
            throw new ApplicationException("Ingen gyldig term for '" + kodenavn + "' funnet i kodeverk '" + xmlEnkeltKodeverk.getNavn() + "'");
        }
        return kode.get().getTerm().get(0).getNavn();
    }
}
