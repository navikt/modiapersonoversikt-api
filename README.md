# Modia Personoversikt API

Modia Personoversikt API tilgjengeliggjør informasjon om brukere i NAV.
Informasjonen blir samlet inn fra flere kilder.
Kildene finnes under [tjenestespesifikasjoner](tjenestespesifikasjoner), der hver modul representerer en kilde.

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

[Team Personoversikt](https://github.com/navikt/info-team-personoversikt)

## Dokumentasjon

Noen nyttige lenker og tips ligger på følgende lenke: https://confluence.adeo.no/pages/viewpage.action?pageId=272512650

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

Om du vil kjøre appen lokatl kan du starte appen ved å kjøre
`mvn exec:java -Dexec.classpathScope="test" -Dexec.mainClass="no.nav.modiapersonoversikt.MainTest"`.

Det vil kjøre appen under `local`-profilen med mockede bønner som er definert
i [LocalBeans.kt](web/src/test/java/no/nav/modiapersonoversikt/LocalBeans.kt).
