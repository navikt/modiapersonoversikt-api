package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import org.joda.time.DateTime;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Traad {
    public final String traadId, temagruppe, journalfortTema, statusKlasse, statusTekst;
    public final List<Melding> meldinger;
    public final DateTime dato;
    public final int antallMeldingerIOpprinneligTraad;
    public final boolean erMonolog;

    public Traad(String traadId, int antallMeldingerIOpprinneligTraad, List<Melding> meldinger) {
        this.traadId = traadId;
        this.antallMeldingerIOpprinneligTraad = antallMeldingerIOpprinneligTraad;
        this.meldinger = meldinger.stream().sorted(comparing(Melding::getVisningsDato).reversed()).collect(toList());
        Melding forsteMelding = this.meldinger.get(this.meldinger.size() - 1);
        Melding sisteMelding = this.meldinger.get(0);
        this.temagruppe = forsteMelding.temagruppeNavn;
        this.journalfortTema = forsteMelding.journalfortTemanavn;
        this.dato = sisteMelding.getVisningsDato();
        this.erMonolog = meldinger.stream()
                .map(Melding::erFraSaksbehandler)
                .distinct()
                .count()
                < 2;
        this.statusKlasse = lagStatusKlasse(sisteMelding);
        this.statusTekst = sisteMelding.statusTekst;
    }

    private String lagStatusKlasse(Melding sisteMelding) {
        String statusklasse = sisteMelding.statusKlasse;
        if (sisteMelding.erVarsel()) {
            return statusklasse;
        }

        if (erMonolog) {
            statusklasse += " monolog";
            if (!sisteMelding.erFraSaksbehandler()) {
                statusklasse += " ubesvart";
            }
        } else {
            statusklasse += " dialog";
            if (sisteMelding.erBesvart()) {
                statusklasse += " besvart";
            }
        }

        return statusklasse;
    }

    public DateTime getDato() {
        return dato;
    }

}
