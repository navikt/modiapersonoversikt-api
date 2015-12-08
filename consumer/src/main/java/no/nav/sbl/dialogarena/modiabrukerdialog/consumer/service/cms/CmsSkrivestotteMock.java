package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class CmsSkrivestotteMock implements CmsSkrivestotte {

    @Override
    public List<SkrivestotteTekst> hentSkrivestotteTekster() {
        URL skrivestotteteksterUrl = getClass().getResource("/mocktekster.json");
        TypeReference<List<SkrivestotteTekst>> type = new TypeReference<List<SkrivestotteTekst>>() {
        };

        return fromJSON(type, skrivestotteteksterUrl);
    }

    private static <T> T fromJSON(final TypeReference<T> type, URL jsonfile) {
        T data = null;
        try {
            data = new ObjectMapper().readValue(jsonfile, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
