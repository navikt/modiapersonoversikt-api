package no.nav.modiapersonoversikt.api.domain.henvendelse;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Traad implements Serializable {
    public final String traadId, temagruppe, journalfortTema, statusKlasse, statusTekst, ikontekst;
    public final List<Melding> meldinger;
    public final DateTime dato;
    public final int antallMeldingerIOpprinneligTraad;
    public final boolean erMonolog;

    public Traad(String traadId, int antallMeldingerIOpprinneligTraad, List<Melding> meldinger) {
        this.traadId = traadId;
        this.meldinger = meldinger.stream().sorted(comparing(Melding::getVisningsDato).reversed()).collect(toList());
        Melding forsteMelding = this.meldinger.get(this.meldinger.size() - 1);
        Melding sisteMelding = this.meldinger.get(0);
        this.antallMeldingerIOpprinneligTraad = sisteMelding.erFerdigstiltUtenSvar ? ++antallMeldingerIOpprinneligTraad : antallMeldingerIOpprinneligTraad;
        this.temagruppe = forsteMelding.temagruppeNavn;
        this.journalfortTema = forsteMelding.journalfortTemanavn;
        this.dato = sisteMelding.getVisningsDato();
        this.erMonolog = meldinger.stream()
                .map(Melding::erFraSaksbehandler)
                .distinct()
                .count()
                < 2;
        this.statusKlasse = lagStatusKlasse(sisteMelding);
        this.ikontekst = lagIkonTekst(sisteMelding);
        this.statusTekst = sisteMelding.statusTekst;
    }

    private String lagStatusKlasse(Melding sisteMelding) {
        String statusklasse = "";

        switch (sisteMelding.meldingstype) {
            case SAMTALEREFERAT_OPPMOTE:
                return "oppmote";
            case SAMTALEREFERAT_TELEFON:
                return "telefon";
            case OPPGAVE_VARSEL:
                return "oppgave";
            case DOKUMENT_VARSEL:
                return "dokument";
            default:
                if (sisteMelding.erFerdigstiltUtenSvar) {
                    statusklasse = "dialog besvart";
                }
                else if (erMonolog) {
                    statusklasse = "monolog";
                    if (!sisteMelding.erFraSaksbehandler()) {
                        statusklasse += " ubesvart";
                    }
                } else {
                    statusklasse = "dialog";
                    if (sisteMelding.erBesvart()) {
                        statusklasse += " besvart";
                    }
                }
        }

        return statusklasse;
    }

    private String lagIkonTekst(Melding melding) {
        switch (melding.meldingstype) {
            case SAMTALEREFERAT_OPPMOTE:
                return "Oppmøte";
            case SAMTALEREFERAT_TELEFON:
                return "Telefon";
            case OPPGAVE_VARSEL:
                return "Oppgave";
            case DOKUMENT_VARSEL:
                return "Dokument";
            case SVAR_OPPMOTE:
            case SVAR_TELEFON:
            case SPORSMAL_MODIA_UTGAAENDE:
                return "Spørsmål";
            default:
                return melding.erBesvart() ? "Besvart" : "Ubesvart";
        }
    }

    public DateTime getDato() {
        return dato;
    }

}
