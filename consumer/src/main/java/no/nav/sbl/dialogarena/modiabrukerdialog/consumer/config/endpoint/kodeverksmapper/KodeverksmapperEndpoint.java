package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class KodeverksmapperEndpoint implements Kodeverksmapper {

    private final String OPPGAVETYPE_ENDPOINT;
    private final String UNDERKATEGORI_ENDPOINT;
    private final String PING_ENDPOINT;
    private HttpClient client;

    KodeverksmapperEndpoint(String oppgavetypeEndpoint, String underkategoriEndpoint, String pingEndpoint) {
        this.OPPGAVETYPE_ENDPOINT = oppgavetypeEndpoint;
        this.UNDERKATEGORI_ENDPOINT = underkategoriEndpoint;
        this.PING_ENDPOINT = pingEndpoint;
        this.client = lagHttpClient();
    }

    private HttpClient lagHttpClient() {
        return HttpClientBuilder.create().build();
    }

    @Override
    public Map<String, String> hentOppgavetype() throws IOException {
        return parseOppgavetypeJson(gjorSporring(OPPGAVETYPE_ENDPOINT));
    }

    @Override
    public Map<String, Behandling> hentUnderkategori() throws IOException {
        return parseUnderkategoriJson(gjorSporring(UNDERKATEGORI_ENDPOINT));
    }

    @Override
    public void ping() throws IOException {
        gjorSporring(PING_ENDPOINT);
    }

    private String gjorSporring(String url) throws IOException {
        HttpResponse response = client.execute(new HttpGet(url));
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException(format("Kall på URL=\"%s\" returnerte respons med status=\"%s\"", url, response.getStatusLine()));
        }
        return hentInnhold(response);
    }

    private String hentInnhold(HttpResponse response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private Map<String, String> parseOppgavetypeJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, new TypeReference<HashMap<String, String>>() {
        });
    }

    private Map<String, Behandling> parseUnderkategoriJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, new TypeReference<HashMap<String, Behandling>>() {
        });
    }
}
