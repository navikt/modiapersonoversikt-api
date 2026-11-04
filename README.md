# Modia Personoversikt API

Modia Personoversikt API tilgjengeliggjør informasjon om brukere i NAV.
Informasjonen blir samlet inn fra flere kilder.
Kildene finnes under [tjenestespesifikasjoner](tjenestespesifikasjoner), der hver modul representerer en kilde.

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

[Team Personoversikt](https://github.com/navikt/info-team-personoversikt)

## Dokumentasjon

Noen nyttige lenker og tips ligger på følgende lenke: https://confluence.adeo.no/pages/viewpage.action?pageId=272512650

### Tilgang til tredjepersoner

Persondata bruker Tilgangsmaskinens bulk-endpoint med `KJERNE_REGELTYPE` for tredjepersoner.
Tilgangskontrollen for hovedpersonen er uendret.
For tredjepersoner med ident kreves godkjenning og et entydig, ikke-tomt PDL-resultat før opplysninger fra eget personoppslag og eventuell kontaktinformasjon fra KRR returneres.
Relasjoner til avviste personer beholdes i de eksisterende responsfeltene, med tomme felt for opplysninger som bare kan hentes fra tredjepersonen.
Dette gjelder også identifiserte personer med uavklart tilgang eller manglende PDL-data.
Skjult ident er `null` der kontrakten tillater det, ellers en tom streng. Skjulte navnelister er tomme, og andre skjulte personopplysninger er tomme eller `null`.
Fullmaktsdata fra Representasjon returneres uavhengig av tredjepersonens tilgang og eget PDL-resultat, inkludert `fullmektig` og `endringslogg`.
For fullmektiger gjelder `skjulingsaarsak` de separate personopplysningene, ikke selve fullmakten.

Innebygde opplysninger på brukerens PDL-post beholder tidligere prioritet og brukes uavhengig av tredjepersonens tilgang og eget PDL-treff.
Forelder-barn-relasjoner bruker innebygde personopplysninger når de finnes, også når relasjonen har ident.
Foreldreansvar bruker innebygd navn når ansvarlig ikke finnes i det godkjente personresultatet.
Gjeldende og historiske vergemål bruker innebygd navn når personoppslaget ikke gir navn.
Dødsbokontakter beholder innebygd navn som alternativ, samt fødselsdato og adresse fra dødsboposten.
Ident skjules fortsatt når tredjepersonen ikke har et godkjent, entydig PDL-resultat.

Hver personoppføring i en relasjon har feltet `skjulingsaarsak`.
`MANGLER_TILGANG` betyr at personen ble avvist. `UAVKLART` betyr at tilgangen eller PDL-dataene ikke kunne avklares.
Feltet er `null` for godkjente personer med et entydig PDL-resultat, for personer uten ident og når innebygde opplysninger brukes.
Dette gjelder sivilstandsrelasjoner, forelder-barn-relasjoner, fullmektiger, gjeldende og historiske vergemål og dødsbokontakter.
I foreldreansvar har `ansvarlig` og `ansvarsubject` hver sin `skjulingsaarsak`.
Frontenden kan dermed vise at en bestemt relasjon finnes, og hvorfor personopplysningene er tomme.
Ved avslag videreformidles også tilgjengelig tittel og begrunnelse fra Tilgangsmaskinen i `tilgangsavslag.title` og `tilgangsavslag.message`.
Andre avslagsdetaljer videreformidles ikke. Feltet utelates når personoppføringen ikke er skjult på grunn av avslag, eller når detaljene mangler eller er ugyldige.
Frontenden viser tittel og melding som tekst, med den generelle skjulingsårsaken som alternativ når detaljer mangler.
Tekniske feil i tilgangssjekken gir også `TILGANGSMASKINEN_TREDJEPARTSPERSONER` i `feilendeSystemer`.
Personer uten ident beholder tidligere mapping fra PDL, inkludert navn og øvrige eksisterende felt i forelder-barn-relasjoner, foreldreansvar, vergemål og dødsbokontakter.
Disse personene kontrolleres ikke av Tilgangsmaskinen og får ikke en skjulingsårsak.
Advokat- og organisasjonskontakter beholder også tidligere mapping.

**TODO før produksjon:** Bekreft at Tilgangsmaskinen har rettet bulk-feilen der gyldig enkelttilgang
kan overstyre avslag fra kjernereglene. Verifiser dette i Navs dev-miljø, inkludert kode 6/7,
skjerming og habilitet, før denne endringen merges til `master`.
En TODO blokkerer ikke deployment: workflowen deployer automatisk til produksjon fra `master`.

## Git hooks

Aktiver pre-commit-hooken for å kjøre linting før hver commit:

```sh
git config core.hooksPath .githooks
```

Hooken kjører `linting.sh` og avbryter commit hvis lintingen feiler.

### Caching

Cacheoppsett er hovedsakelig beskrevet ved bruk av annotasjon og benytter seg av Springs cache abstraction og en
underliggende caffeine-cache-implementasjon.

Tjenestekall mot NORG caches i to forskjellige cacher. Se hver enkelt tjeneste for eksakt hvilke kall som bruker hvilken
cache.
Det er hovedsakelig en forskjell på time-to-live og key-generering. Den ene cachen har caching som tar hensyn til
innlogget saksbehandler, og har en kort time-to-live.
Den andre cachen har data som ikke endres pr bruker og caches derfor per node. Denne har også en lengre time-to-live for
at dataene skal leve frem til neste faste
cache-populeringsjobb. Se *Faste jobber* for mer informasjon om denne.

Tjenestekall mot andre tjenester har egne cacher.

Cacheoppsettet er ikke blocking, dvs at dersom to tråder spør med like keys vil cachen slippe gjennom to kall til den
underliggende tjenesten.

### Faste jobber

* Oppdatering av kodeverk for Arkivtemaer

Denne jobben oppdaterer kodeverk for Arkivtemaer ved midnatt hver dag.

## Starte appen lokalt

Om du vil kjøre appen lokalt kan du starte appen ved å kjøre
```
mvn clean install
cd web
mvn exec:java -Dexec.classpathScope="test" -Dexec.mainClass="no.nav.modiapersonoversikt.MainTest"
```

Det vil kjøre appen under `local`-profilen med mockede bønner som er definert
i [LocalBeans.kt](web/src/test/java/no/nav/modiapersonoversikt/LocalBeans.kt).
