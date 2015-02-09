package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import java.util.List;

public interface HjelpetekstIndex {

    void indekser(List<Hjelpetekst> hjelpetekster);

    List<Hjelpetekst> sok(String frisok);
}
