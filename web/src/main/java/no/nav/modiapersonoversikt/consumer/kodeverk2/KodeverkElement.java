package no.nav.modiapersonoversikt.consumer.kodeverk2;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static no.nav.modiapersonoversikt.consumer.kodeverk2.Kodeverk.Nokkel.*;

class KodeverkElement {

    private final Map<Kodeverk.Nokkel, String> koder;

    KodeverkElement(Map<Kodeverk.Nokkel, String> kodeverkMap) {
        koder = new HashMap<>();
        koder.put(SKJEMANUMMER, kodeverkMap.get(SKJEMANUMMER));
        koder.put(BESKRIVELSE, kodeverkMap.get(BESKRIVELSE));
        koder.put(VEDLEGGSID, kodeverkMap.get(VEDLEGGSID));
        koder.put(GOSYS_ID, kodeverkMap.get(GOSYS_ID));
        koder.put(TEMA, kodeverkMap.get(TEMA));
        koder.put(TITTEL, kodeverkMap.get(TITTEL));
        koder.put(TITTEL_EN, kodeverkMap.get(TITTEL_EN));
        koder.put(TITTEL_NN, kodeverkMap.get(TITTEL_NN));
        koder.put(URL, kodeverkMap.get(URL));
        koder.put(URLENGLISH, kodeverkMap.get(URLENGLISH));
        koder.put(URLNEWNORWEGIAN, kodeverkMap.get(URLNEWNORWEGIAN));
        koder.put(URLPOLISH, kodeverkMap.get(URLPOLISH));
        koder.put(URLFRENCH, kodeverkMap.get(URLFRENCH));
        koder.put(URLSPANISH, kodeverkMap.get(URLSPANISH));
        koder.put(URLGERMAN, kodeverkMap.get(URLGERMAN));
        koder.put(URLSAMISK, kodeverkMap.get(URLSAMISK));
    }

    Map<Kodeverk.Nokkel, String> getKoderMap() {
        return unmodifiableMap(koder);
    }

}
