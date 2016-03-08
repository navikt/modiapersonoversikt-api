package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;

import java.util.List;
import java.util.Set;

public class DokumentMetadataResultatWrapper {

    public List<DokumentMetadata> dokumentMetadata;
    public Set<Baksystem> feiledeSystemer;

    public DokumentMetadataResultatWrapper(List<DokumentMetadata> dokumentMetadata, Set<Baksystem> feiledeSystemer) {
        this.dokumentMetadata = dokumentMetadata;
        this.feiledeSystemer = feiledeSystemer;
    }
}
