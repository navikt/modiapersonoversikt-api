package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class CmsSkrivestotteEnonic implements CmsSkrivestotte {
    private static final Logger log = LoggerFactory.getLogger(CmsSkrivestotteEnonic.class);
    private static final String currentDomain = optionalProperty("skrivestotte.domain", "LOADBALANCER_MODIAPERSONOVERSIKT_API_URL")
            .orElseGet(() -> {
                log.error("Fant ikke en verdi for `customDomain`");
                return "";
            });

    @Override
    public List<SkrivestotteTekst> hentSkrivestotteTekster() {
        String modiapersonOversiktUrl = currentDomain + "/modiapersonoversikt-skrivestotte/skrivestotte";
        try {
            Future<Content> resp = Async.newInstance().execute(Request.Get(modiapersonOversiktUrl));
            String content = resp.get(10000, TimeUnit.SECONDS).toString();
            JsonObject data = new JsonParser().parse(content).getAsJsonObject();

            return data
                    .entrySet()
                    .stream()
                    .map(CmsSkrivestotteEnonic::getSkrivestotteTekst)
                    .collect(toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SkrivestotteTekst getSkrivestotteTekst(Map.Entry<String, JsonElement> entry) {
        JsonObject tekstdata = entry.getValue().getAsJsonObject();
        String key = tekstdata.getAsJsonPrimitive("id").getAsString();
        String overskrift = tekstdata.getAsJsonPrimitive("overskrift").getAsString();
        String[] tags = lagTagsListe(tekstdata.getAsJsonArray("tags"));
        Map<String, String> innhold = lagInnholdMap(tekstdata.getAsJsonObject("innhold"));

        return new SkrivestotteTekst(
                key,
                overskrift,
                innhold,
                tags
        );
    }
    private static String[] lagTagsListe(JsonArray array) {
        String[] liste = new String[array.size()];
        for (int i = 0; i < liste.length; i++) {
            liste[i] = array.get(i).getAsJsonPrimitive().getAsString();
        }
        return liste;
    }

    private static Map<String, String> lagInnholdMap(JsonObject innhold) {
        return innhold
                .entrySet()
                .stream()
                .map((entry) -> new ImmutablePair<>(
                        entry.getKey(),
                        entry.getValue().getAsJsonPrimitive().getAsString()
                ))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    private static Optional<String> optionalProperty(String... keys) {
        return Stream.of(keys)
                .map(System::getProperty)
                .filter(Objects::nonNull)
                .findFirst();
    }

}
