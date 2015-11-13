# Modia brukerdialog

## Dokumentasjon

### Caching

Cacheoppsett er hovedsakelig beskrevet i `cacheconfig.xml` og benytter seg av Springs cache abstraction og en underliggende Ehcache-implementasjon.

Tjenestekall mot NORG caches i to forskjellige cacher. Se `cacheconfig.xml` for eksakt hvilke kall som bruker hvilken cache.
Det er hovedsakelig en forskjell på time-to-live og key-generering. Den ene cachen har caching som tar hensyn til innlogget saksbehandler, og har en kort time-to-live.
Den andre cachen har data som ikke endre pr bruker og caches derfor bare på request. Denne har også en lengre time-to-live for at dataene skal leve frem til neste faste
cache-populeringsjobb. Se *Faste jobber* for mer informasjon om denne.

Tjenestekall mot andre tjenester har egne cacher.

Cacheimplementasjonen til Ehcache er ikke blocking, dvs at dersom to tråder spør med like keys vil cachen slippe gjennom to kall til den underliggende tjenesten.

### Faste jobber

* Populering av cache for ansatte i Enheter fra NORG via `ScheduledAnsattListePrefetch` to ganger daglig

Denne jobben henter alle enheter og deretter henter alle ansatte i de respektive enhetene. Dette gjøres med kall mot NORG.
Bakgrunnen for at denne har blitt til en fast jobb var et ønske om å få ned svartidene for henting av ansatte for alle brukere.
Tidspunktet for kjøring av jobben bestemmes av en property `prefetch.norg.ansattliste.schedule` under fasitressursen `modiabrukerdialog.properties`. Formatet er Springs `@Scheduled`
cron-format. Jobben bør kjøre utenfor saksbehandlernes arbeidstider, som også er tider når NORG har kapasitet til å svare raskere.

## Oppstart av appen på Jetty
- Hvis man får OutOfMemoryError ved lokal kjøring så kan man sette opp PermGen space i prosessen som kjører StartJetty,
ved å legge til -XX:MaxPermSize=256M som VM Option.
I IntelliJ gjøres dette i ved å klikke "Edit Configurations" i nedtrekksmenyen for prosessen "StartJetty" i topplinjen.

### IntelliJ

- Opprett en run configuration for klassen StartJetty
- Endre working directory til <root>\modiabrukerdialog\web

