package no.nav.sbl.dialogarena.sak.viewdomain.widget;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;

public class ModiaSakstema extends Sakstema {

    public boolean harTilgang;

    public ModiaSakstema(Sakstema sakstema) {
        this.temakode = sakstema.temakode;
        this.temanavn = sakstema.temanavn;
        this.erGruppert = sakstema.erGruppert;
        this.behandlingskjeder = sakstema.behandlingskjeder;
        this.dokumentMetadata = sakstema.dokumentMetadata;
        this.tilhorendeSaker = sakstema.tilhorendeSaker;
        this.feilkoder = sakstema.feilkoder;

    }

    public ModiaSakstema withTilgang(boolean harTilgang){
        this.harTilgang = harTilgang;
        if (!harTilgang) {
            harIkkeTilgang();
        }
        return this;
    }

    private void harIkkeTilgang() {
        for (DokumentMetadata dokument : dokumentMetadata) {
            dokument.withFeilWrapper(Feilmelding.SIKKERHETSBEGRENSNING);
        }
    }

}
