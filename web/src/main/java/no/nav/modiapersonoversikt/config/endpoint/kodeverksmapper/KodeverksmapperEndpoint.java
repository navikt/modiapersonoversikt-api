package no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.common.rest.client.RestClient;
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private static final OkHttpClient client = RestClient.baseClient();

    KodeverksmapperEndpoint(String oppgavetypeEndpoint, String underkategoriEndpoint, String pingEndpoint) {
        this.OPPGAVETYPE_ENDPOINT = oppgavetypeEndpoint;
        this.UNDERKATEGORI_ENDPOINT = underkategoriEndpoint;
        this.PING_ENDPOINT = pingEndpoint;
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
        Response response = client.newCall(new Request.Builder().url(url).build()).execute();
        if (response.code() != 200) {
            throw new IOException(format("Kall på URL=\"%s\" returnerte respons med status=\"%s\"", url, response.code()));
        }
        return hentInnhold(response);
    }

    private String hentInnhold(Response response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
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
