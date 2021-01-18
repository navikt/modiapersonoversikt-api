package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;

import java.util.ArrayList;
import java.util.List;

public interface SakerService {
    class Resultat {
        public List<Sak> saker;
        public List<String> feiledeSystemer;

        public Resultat() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        public Resultat(List<Sak> saker, List<String> feiledeSystemer) {
            this.saker = saker;
            this.feiledeSystemer = feiledeSystemer;
        }
    }

    Resultat hentSaker(String fnr);

    @Deprecated
    List<Sak> hentSammensatteSaker(String fnr);

    @Deprecated
    List<Sak> hentPensjonSaker(String fnr);

    void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak, String enhet) throws JournalforingFeilet;
}
