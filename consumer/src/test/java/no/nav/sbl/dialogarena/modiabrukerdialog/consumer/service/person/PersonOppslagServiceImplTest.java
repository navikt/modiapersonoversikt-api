package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.person;

import no.nav.json.JsonUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.PersonOppslagResponse;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.UtenlandskIdentifikasjonsnummer;
import org.junit.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonOppslagServiceImplTest {

    @Test
    public void skalMappeFraJsonResponseTilPersondokumentObjekt() {
        String idnummer = "9999";
        UUID uuid = java.util.UUID.randomUUID();
        String land = "POL";

        String opplysningsId = "qwe123";
        String registrertINav = "2019-01-08T15:26:14.714Z";
        String master = "NAV";
        String kilde = "Krankenkasse";
        String idnummerType = "test";
        String systemkilde = "srvperson-mottak";
        String ident = "z999999";
        String gyldigFom = "2018-01-31";
        String jsonResponse = "{\n" +
                "  \"personidenter\": {\n" +
                "    \"folkeregisteridenter\": [\n" +
                "      {\n" +
                "        \"idNummer\": \"10108000398\",\n" +
                "        \"idNummertype\": \"FNR\",\n" +
                "        \"opplysningsId\": \"abc\",\n" +
                "        \"registrertINAV\": \"2018-12-14T10:04:33.938Z\",\n" +
                "        \"master\": \"Folkeregisteret\",\n" +
                "        \"kilde\": \"Synutopia\",\n" +
                "        \"idNummerType\": \"\",\n" +
                "        \"systemKilde\": \"ukjent\",\n" +
                "        \"registrertAv\": \"ukjent\",\n" +
                "        \"gyldigFom\": \"2018-05-09\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"utenlandskeIdentifikasjonsnummere\": [\n" +
                "      {\n" +
                "        \"idNummer\": \"" + idnummer + "\",\n" +
                "        \"utstederland\": \"" + land + "\",\n" +
                "        \"opplysningsId\": \"" + opplysningsId + "\",\n" +
                "        \"registrertINAV\": \"" + registrertINav + "\",\n" +
                "        \"master\": \"" + master + "\",\n" +
                "        \"kilde\": \"" + kilde + "\",\n" +
                "        \"idNummertype\": \"" + idnummerType + "\",\n" +
                "        \"systemKilde\": \"" + systemkilde + "\",\n" +
                "        \"registrertAv\": \"" + ident + "\",\n" +
                "        \"gyldigFom\": \"" + gyldigFom + "\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"lineage\": {\n" +
                "    \"nodeId\": \"" + uuid + "\"\n" +
                "  }\n" +
                "}";


        PersonOppslagResponse persondokument = JsonUtils.fromJson(jsonResponse, PersonOppslagResponse.class);

        UtenlandskIdentifikasjonsnummer utenalandskId = persondokument.getPersonidenter().getUtenlandskeIdentifikasjonsnummere().get(0);
        assertEquals(idnummer, utenalandskId.getIdNummer());
        assertEquals(land, utenalandskId.getUtstederland());
        assertEquals(opplysningsId, utenalandskId.getOpplysningsId());
        assertEquals(registrertINav, utenalandskId.getRegistrertINAV().toString());
        assertEquals(master, utenalandskId.getMaster());
        assertEquals(kilde, utenalandskId.getKilde());
        assertEquals(systemkilde, utenalandskId.getSystemKilde());
        assertEquals(ident, utenalandskId.getRegistrertAv());
    }
}
