---
applyTo: "**"
excludeAgent: "cloud-agent"
---

# Code review

## Avgrensning

- Prioriter konkrete feil introdusert av endringen. Les relevante kallesteder og tester, og beskriv utløsende scenario og konsekvens.
- Skill dokumenterte feil fra usikkerhet. Ikke foreslå formatering som lint håndterer, eller refaktorering uten en konkret feil.

## Tilgangskontroll

- Følg kontrollflyten fra endpoint til tjenestekall. Kontroller at autentisering og relevante tilgangspolicyer håndheves før beskyttede data utleveres eller endringer utføres.
- Bruk `Tilgangskontroll` og `Policies` i `web/src/main/java/no/nav/modiapersonoversikt/infrastructure/tilgangskontroll/` som utgangspunkt. Innlogging eller `tilgangTilModia` alene dokumenterer ikke tilgang til en person.
- Kontroller at ressursen tilhører personen det sjekkes tilgang til. For henvendelser finnes `henvendelseTilhorerBruker`; et innsendt fødselsnummer er ikke bevis på tilhørighet.
- Ved endrede policyer: kontroller at skjerming, adressebeskyttelse og relevante tema-/enhetsbegrensninger bevares. Feil eller uavklart tilgang skal ikke gi tilgang.
- Ved endret caching: kontroller at cachetreff ikke omgår tilgangskontroll eller deler tilgangsavhengige svar mellom saksbehandlere.

## Personvern og logging

- Flagg personopplysninger som fødselsnummer, aktør-ID, navn, adresse og meldingsinnhold i vanlige logger. Flagg også tokens og andre secrets.
- Følg data inn i loggkall, exception-meldinger, MDC, tracing og HTTP-interceptorer. Hele request-/response-objekter, URL-er og `toString()` kan lekke opplysninger indirekte.
- Skill vanlig logging fra etablert audit-logging i `infrastructure/naudit/`. Bevar påkrevd sporbarhet og begrens audit-data til nødvendige identifikatorer. `teamLog` er ikke en generell tillatelse til å logge persondata.
- Kontroller at feilsvar ikke eksponerer persondata eller detaljer fra nedstrømstjenester. Bruk syntetiske persondata i tester.

## Regresjoner og tester

- Ved endret tilgangskontroll: flagg konkrete tilfeller uten testdekning, særlig avvist tilgang, ressurs for en annen person og feil i tilgangsoppslag.
- Ved API-endringer: kontroller kompatibilitet med eksisterende konsumenter. Ikke presenter feil eller delvis suksess som fullført handling.
- Flagg manglende tester for konkrete regresjonsscenarioer. Kontroller at snapshot-endringer viser ønsket atferd, ikke skjuler feil.
