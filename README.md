# Modia utbetalinger #

## Mangler fra tjenesten ##
* Trekk kommer som positive tall.
    * Nødt å basere seg på om beskrivelsen inneholder "trekk" eller "skatt" for så å invertere verdien
    * Hva skjer med f.eks "Tilbakebetalt skatt"?
* Ulik lokalisering av beskrivelsestekst avhengig av baksystemet
    * Ved data fra "UR" ligger beskrivelsen under: Hvert bilag, hver posteringsdetalj, feltet "kontoBeskrHoved"
    * Ved data fra "abetal" ligger beskrivelsen som tekstmelding på utbetalingen
        * Denne beskrivelsen er feil. "AAP - grunnsats" burde mest sannsynlig vært "Arbeidsavklaringspenger"
* Flagg som indikterer skattetrekk er ønskelig
* Konsistens merking av hvilken tjeneste dataen faktisk kommer fra
    * Tilfellet nå er at det enten kommer "abetal", eller "null". Dette holder for nå, men det kunne vært ønskelig å faktisk returnere hvilken tjeneste dataen kommer fra uansett
* Statuskoder som er like, har ulik betydning basert på systemet dataen opprinnelig kommer fra
    * Ønskelig at tjenesten vi kommuniserer med håndterer mapping til felles statuskoder, istede for at vi er nødt til å separere dataen i to; "abetal" og "ur" når vi får det. Vi bør kunne håndtere dataen som det skulle kommet fra ett system
