package no.nav.modiapersonoversikt.consumer.kodeverk2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Kodeverkimlpmentasjon som leser inn kodeverk på json-format
 */
public class JsonKodeverk extends BaseKodeverk {

    private static final Logger LOGGER = getLogger(JsonKodeverk.class);

    public JsonKodeverk(InputStream json) {
        try {
            traverseSkjemaerAndInsertInMap(getSkjemaer(json));
        } catch (Exception e) {
            LOGGER.error("Klarte ikke å parse kodeverk-info", e);
            throw new ApplicationException("Klarte ikke å parse kodeverk-info", e);
        }
    }

    private ArrayNode getSkjemaer(InputStream json) throws IOException {
        return (ArrayNode) new ObjectMapper().readTree(json).get("Skjemaer");
    }

    private void traverseSkjemaerAndInsertInMap(ArrayNode kodeverkArray) {
        for (JsonNode node : kodeverkArray) {
            Map<Kodeverk.Nokkel, String> skjema = new HashMap<>();
            Map<Kodeverk.Nokkel, String> vedlegg = new HashMap<>();
            byggOppSkjema(node, skjema);
            dbSkjema.put(getFieldValue(node, "Skjemanummer"), new KodeverkElement(skjema));
            if (!"".equals(getOptionalFieldValue(node, "Vedleggsid"))) {
                byggOppSkjema(node, vedlegg);
                dbVedlegg.put(getFieldValue(node, "Vedleggsid"), new KodeverkElement(vedlegg));
            }
        }
    }

    private void byggOppSkjema(JsonNode node, Map<Kodeverk.Nokkel, String> map) {
        map.put(Kodeverk.Nokkel.SKJEMANUMMER, getOptionalFieldValue(node, "Skjemanummer"));
        map.put(Kodeverk.Nokkel.GOSYS_ID, getOptionalFieldValue(node, "Gosysid"));
        map.put(Kodeverk.Nokkel.VEDLEGGSID, getOptionalFieldValue(node, "Vedleggsid"));
        map.put(Kodeverk.Nokkel.TEMA, getOptionalFieldValue(node, "Tema"));
        map.put(Kodeverk.Nokkel.BESKRIVELSE, getOptionalFieldValue(node, "Beskrivelse (ID)"));
        map.put(Kodeverk.Nokkel.TITTEL, getFieldValue(node, "Tittel"));
        map.put(Kodeverk.Nokkel.TITTEL_EN, getFieldValue(node, "Tittel_en"));
        map.put(Kodeverk.Nokkel.TITTEL_NN, getFieldValue(node, "Tittel_nn"));
        map.put(Kodeverk.Nokkel.URL, getOptionalFieldValue(node, "Lenke"));
        map.put(Kodeverk.Nokkel.URLENGLISH, getOptionalFieldValue(node, "Lenke engelsk skjema"));
        map.put(Kodeverk.Nokkel.URLNEWNORWEGIAN, getOptionalFieldValue(node, "Lenke nynorsk skjema"));
        map.put(Kodeverk.Nokkel.URLPOLISH, getOptionalFieldValue(node, "Lenke polsk skjema"));
        map.put(Kodeverk.Nokkel.URLFRENCH, getOptionalFieldValue(node, "Lenke fransk skjema"));
        map.put(Kodeverk.Nokkel.URLSPANISH, getOptionalFieldValue(node, "Lenke spansk skjema"));
        map.put(Kodeverk.Nokkel.URLGERMAN, getOptionalFieldValue(node, "Lenke tysk skjema"));
        map.put(Kodeverk.Nokkel.URLSAMISK, getOptionalFieldValue(node, "Lenke samisk skjema"));
    }

    private String getFieldValue(JsonNode node, String fieldName) {

        if (node.has(fieldName)) {
            return node.get(fieldName).asText();
        } else {
            LOGGER.error("Mangler obligatorisk felt {} i kodeverket (json)");
            throw new ApplicationException("Mangler felt " + fieldName + " i kodeverket json");
        }
    }

    private String getOptionalFieldValue(JsonNode node, String fieldName) {
        if (node.has(fieldName)) {
            return node.get(fieldName).asText();
        } else {
            return "";
        }
    }
}
