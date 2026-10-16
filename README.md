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

Spring Cache i `web` bruker Valkey-instansen `modiapersonoversikt-api` via Nais. Den deles mellom podene i samme miljø.
Nøklene har et versjonert prefiks, og hver cache har en utløpstid definert i
`web/src/main/java/no/nav/modiapersonoversikt/infrastructure/cache/CacheConfig.kt`.
Verdier serialiseres med Springs `GenericJacksonJsonRedisSerializer` (Jackson 3). Jackson-konfigurasjonen
begrenser tillatte typer til cachemodellene og utvalgte standardtyper. Nye modelltyper kan kreve at listen utvides.
Nøkkelgeneratoren bruker også Jackson 3. Eksisterende Jackson 2-bruk utenfor cacheoppsettet er uendret.
Prefikset er `cache:v2`, slik at verdier fra det tidligere, egenutviklede formatet ikke leses.
Nøklene er JSON i klartekst med klasse, metode, Nav-ident og argumenter. Fødselsnumre og Nav-identer kan dermed
leses i Valkey og i `db.statement` på Redis-spans i Tempo. I prod er Redis-spans slått av med
`OTEL_INSTRUMENTATION_LETTUCE_ENABLED=false` i `web/.nais/prod.yml`. I dev er de på.
Valkey beholder oppføringer ved deploy til de utløper. Ved tilkoblingsfeil henter appen data direkte fra kildesystemet
uten å cache svaret. Sett `cache.backend=caffeine` ved lokal kjøring eller som midlertidig rollback via ny deploy.

`proxy` og klienter som bruker `CacheUtils` eller Caffeine direkte i `web`, fortsetter med lokale cacher.

Tjenestekall mot NORG caches i to forskjellige cacher. Se hver enkelt tjeneste for eksakt hvilke kall som bruker hvilken
cache.
Det er hovedsakelig en forskjell på time-to-live og key-generering. Den ene cachen har caching som tar hensyn til
innlogget saksbehandler, og har en kort time-to-live.
Den andre cachen har data som ikke endres pr bruker og caches derfor per node. Denne har også en lengre time-to-live for
at dataene skal leve frem til neste faste
cache-populeringsjobb. Se *Faste jobber* for mer informasjon om denne.

Tjenestekall mot andre tjenester har egne cacher. Valkey oppdateres ikke automatisk når PDL endrer data;
PDL-oppføringene utløper etter én time.

Cacheoppsettet er ikke blocking, dvs at dersom to tråder spør med like keys vil cachen slippe gjennom to kall til den
underliggende tjenesten.

### Teste Valkey lokalt

Start testinstansen fra repoets rotmappe:

```sh
docker-compose up -d --wait
mvn -pl web -am -Dkotlin.compiler.daemon=false -Dkotlin.compiler.execution.strategy=in-process \
  -Dtest=ValkeyCacheIntegrationTest -Dsurefire.failIfNoSpecifiedTests=false \
  -Dvalkey.integration.enabled=true test
docker-compose down
```

Testen kobler seg til `127.0.0.1:16379` med testpassordet fra Compose-fila. Uten
`-Dvalkey.integration.enabled=true` blir integrasjonstesten hoppet over i vanlig Maven-kjøring.
Hvis du bruker en annen lokal instans, kan du overstyre `valkey.test.host`, `valkey.test.port` og
`valkey.test.password` med `-D`-flagg. Testinstansen har ingen persistent lagring og er kun tilgjengelig
fra localhost; ikke bruk den til reelle personopplysninger.

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
