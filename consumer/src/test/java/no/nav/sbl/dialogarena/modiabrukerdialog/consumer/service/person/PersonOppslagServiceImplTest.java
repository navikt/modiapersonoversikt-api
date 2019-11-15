package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.person;

import no.nav.json.JsonUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.AdvokatSomAdressat;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.KontaktiformasjonForDoedsbo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.PersonOppslagResponse;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonOppslagServiceImplTest {

    @Test
    public void skalMappeFraJsonResponseTilPersondokumentObjekt() {
        String jsonResponse = "{  \n" +
                "\"kontaktinformasjonForDoedsbo\": [\n" +
                "    {\n" +
                "      \"adressat\": {\n" +
                "        \"advokatSomAdressat\": {\n" +
                "          \"kontaktperson\": {\n" +
                "            \"etternavn\": \"Gulrot\",\n" +
                "            \"fornavn\": \"Oransje\",\n" +
                "            \"mellomnavn\": \"Gul\"\n" +
                "          },\n" +
                "          \"organisasjonsnavn\": \"Advokat AS\",\n" +
                "          \"organisasjonsnummer\": 123456789\n" +
                "        },\n" +
                "        \"kontaktpersonMedIdNummerSomAdressat\": {\n" +
                "          \"idNummer\": 12345678910\n" +
                "        },\n" +
                "        \"kontaktpersonUtenIdNummerSomAdressat\": {\n" +
                "          \"foedselsdato\": \"string\",\n" +
                "          \"navn\": {\n" +
                "            \"etternavn\": \"Gulrot\",\n" +
                "            \"fornavn\": \"Oransje\",\n" +
                "            \"mellomnavn\": \"Gul\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"organisasjonSomAdressat\": {\n" +
                "          \"kontaktperson\": {\n" +
                "            \"etternavn\": \"Gulrot\",\n" +
                "            \"fornavn\": \"Oransje\",\n" +
                "            \"mellomnavn\": \"Gul\"\n" +
                "          },\n" +
                "          \"organisasjonsnavn\": \"Organisasjon AS\",\n" +
                "          \"organisasjonsnummer\": 123456789\n" +
                "        }\n" +
                "      },\n" +
                "      \"adresselinje1\": \"TestAdresselinje1\",\n" +
                "      \"adresselinje2\": \"TestAdresselinje2\",\n" +
                "      \"gyldigFom\": \"2018-10-26\",\n" +
                "      \"gyldigTom\": \"2018-10-26\",\n" +
                "      \"kilde\": \"KILDE_DSF\",\n" +
                "      \"landkode\": \"POL\",\n" +
                "      \"master\": \"Folkeregisteret\",\n" +
                "      \"opplysningsId\": \"85dc7a9c-7e5c-41da-87c9-0320fb74165d\",\n" +
                "      \"postnummer\": \"string\",\n" +
                "      \"poststedsnavn\": \"string\",\n" +
                "      \"registrertAv\": \"Z990000\",\n" +
                "      \"registrertINAV\": \"2018-10-26T12:47:18.752Z[UTC]\",\n" +
                "      \"skifteform\": \"offentlig\",\n" +
                "      \"systemKilde\": \"srveessi\",\n" +
                "      \"utstedtDato\": \"string\"\n" +
                "    }\n" +
                "  ] \n" +
                "}";


        PersonOppslagResponse persondokument = JsonUtils.fromJson(jsonResponse, PersonOppslagResponse.class);

        KontaktiformasjonForDoedsbo kd = persondokument.getKontaktinformasjonForDoedsbo().get(0);

        assertEquals(kd.getAdresselinje1(), "TestAdresselinje1");
        assertEquals(kd.getLandkode(), "POL");

        AdvokatSomAdressat addr = kd.getAdressat().getAdvokatSomAdressat();

        assertEquals(addr.getOrganisasjonsnavn(), "Advokat AS");
    }
}
