# Modia utbetalinger #

## Mangler fra tjenesten ##
* Trekk kommer som positive tall.
    * Nødt å basere seg på om beskrivelsen inneholder "trekk" eller "skatt" for så å invertere verdien
    * Hva skjer med f.eks "Tilbakebetalt skatt"?
    * Flagg som indikterer skattetrekk er ønskelig

* Inkonsistens mellom UR og Abetal
    * KildeNavn-feltet er kun satt når data kommer fra Abetal - ikke fra UR (null)
    * YtelseBeskrivelse for en utbetaling ligger i forskjellige felter:
        * Tekstmelding for Abetal
        * YtelseBeskrivelse i Bilag for UR
    * Periode i utbetaling er kun satt for Abetal, ikke UR

* Flere felt i Posteringsdetaljer kan være null (ikke satt).
    * Dette gjelder ihvertfall Antall, Sats og Spesifikasjon.
    * Evt. default-verdi    er for disse feltene hadde vært ønskelig.

* Noen Utbetalinger (f.eks. Kontantstøtte, Dagpenger etc.) kommer i duplikater ved tjenestekall.

* Noen utbetalinger som kommer fra Abetal er ikke merket at kommer fra Abetal.
* Ingen utbetalinger fra UR er merket med UR som kilde.

* Denne beskrivelsen er feil: "AAP - grunnsats" burde mest sannsynlig vært "Arbeidsavklaringspenger"

* Statuskoder som er like, har ulik betydning basert på systemet dataen opprinnelig kommer fra
    * Ønskelig at tjenesten vi kommuniserer med håndterer mapping til felles statuskoder, istedet for at vi er nødt til å separere dataen i to; "Abetal" og "UR" når vi får det.
    * Tjenestedokumentasjonen på Confluence lister kun opp UR-statuskoder, er det kun disse man skal forholde seg til?

* Tjenesten implementer ikke en ping-operasjon.

* Vi bør kunne håndtere dataene som om de skulle komme fra ett system.


