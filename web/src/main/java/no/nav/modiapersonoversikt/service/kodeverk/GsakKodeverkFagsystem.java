package no.nav.modiapersonoversikt.service.kodeverk;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class GsakKodeverkFagsystem implements Serializable {

    public static class Parser extends GsakKodeParser {

        private static final String KODE = "kode";
        private static final String DECODE = "decode";

        public static Map<String, String> parse() {
            try (
                    InputStream fagsystemer = GsakKodeverkFagsystem.class.getResourceAsStream("/xml/fagsystem.xml")
            ) {
                Document fagsystemerDocument = parseDocument(fagsystemer);
                List<Node> fagsystemNodes = compileAndEvaluate(fagsystemerDocument, "//fagsystemListe/fagsystem/gosys[not(@erGyldig = 'false')]");
                final Map<String, String> fagsystemMap = new HashMap<>();
                fagsystemNodes.forEach(node -> {
                    String fagsystemkode = getParentNodeValue(node, KODE);
                    String fagsystemnavn = getNodeValue(node, DECODE);
                    fagsystemMap.put(fagsystemkode, fagsystemnavn);
                });
                return fagsystemMap;

            } catch (Exception e) {
                throw new ApplicationException("Kunne ikke laste inn gsak kodeverk", e);
            }
        }

    }

}
