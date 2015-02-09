package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import java.util.List;

import static java.util.Arrays.asList;

public class Hjelpetekst {

    public final String tittel, innhold;
    public List<String> tags;

    public Hjelpetekst(String tittel, String innhold, String ... tags) {
        this.tittel = tittel;
        this.innhold = innhold;
        this.tags = asList(tags);
    }
}
