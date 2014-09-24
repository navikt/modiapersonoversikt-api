package no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk;

import no.nav.modig.core.exception.ApplicationException;
import org.apache.commons.collections15.Closure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;

/**
 * Fagsystem kodeverk. Henter mapping mellom fagsystemkode og fagsystemnavn.
 */

public abstract class GsakKodeFagsystem implements Serializable {

    public static class Parser extends GsakKodeParser {

        private static final String KODE = "kode";
        private static final String DECODE = "decode";

        public static Map<String, String> parse() {
            try (
                    InputStream fagsystemer = GsakKodeFagsystem.class.getResourceAsStream("/xml/fagsystem.xml");
            ) {
                Document fagsystemerDocument = parseDocument(fagsystemer);
                List<Node> fagsystemNodes = compileAndEvaluate(fagsystemerDocument, "//fagsystemListe/fagsystem/gosys[not(@erGyldig = 'false')]");
                final Map<String, String> fagsystemMap = new HashMap<>();
                on(fagsystemNodes).forEach(new Closure<Node>() {
                    @Override
                    public void execute(Node node) {
                        String fagsystemkode = getParentNodeValue(node, KODE);
                        String fagsystemnavn = getNodeValue(node, DECODE);
                        fagsystemMap.put(fagsystemkode, fagsystemnavn);
                    }
                });
                return fagsystemMap;

            } catch (Exception e) {
                throw new ApplicationException("Kunne ikke laste inn gsak kodeverk", e);
            }
        }

    }

}
