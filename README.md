# Modia brukerdialog

Modia er en intern arbeidsflate som inneholder kjerneinformasjon om brukeren og som gir medarbeiderne i NAV oversikt over brukerens forhold til NAV.

## Dokumentasjon

Noen nyttige lenker og tips ligger på følgende lenke: https://confluence.adeo.no/pages/viewpage.action?pageId=272512650

### Caching

Cacheoppsett er hovedsakelig beskrevet i `cacheconfig.xml` og benytter seg av Springs cache abstraction og en underliggende Ehcache-implementasjon.

Tjenestekall mot NORG caches i to forskjellige cacher. Se `cacheconfig.xml` for eksakt hvilke kall som bruker hvilken cache.
Det er hovedsakelig en forskjell på time-to-live og key-generering. Den ene cachen har caching som tar hensyn til innlogget saksbehandler, og har en kort time-to-live.
Den andre cachen har data som ikke endres pr bruker og caches derfor per node. Denne har også en lengre time-to-live for at dataene skal leve frem til neste faste
cache-populeringsjobb. Se *Faste jobber* for mer informasjon om denne.

Tjenestekall mot andre tjenester har egne cacher.

Cacheimplementasjonen til Ehcache er ikke blocking, dvs at dersom to tråder spør med like keys vil cachen slippe gjennom to kall til den underliggende tjenesten.

### Faste jobber

* Populering av cache for ansatte i Enheter fra NORG via `ScheduledAnsattListePrefetch` to ganger daglig

Denne jobben henter alle enheter og deretter henter alle ansatte i de respektive enhetene. Dette gjøres med kall mot NORG.
Bakgrunnen for at denne har blitt til en fast jobb var et ønske om å få ned svartidene for henting av ansatte for alle brukere.
Tidspunktet for kjøring av jobben bestemmes av en property `PREFETCH_NORG_ANSATTLISTE_SCHEDULE` under fasitressursen `modiabrukerdialog.properties`. Formatet er Springs `@Scheduled`
cron-format. Jobben bør kjøre utenfor saksbehandlernes arbeidstider, som også er tider når NORG har kapasitet til å svare raskere.

Dersom propertyen ikke finnes eller inneholder feil vil applikasjonen feile under oppstart med tilsvarende

    // Propertyen finnes ikke
    java.lang.IllegalStateException: Encountered invalid @Scheduled method 'prefetchAnsattListe': Could not resolve placeholder 'PREFETCH_NORG_ANSATTLISTE_SCHEDULE' in string value "${PREFETCH_NORG_ANSATTLISTE_SCHEDULE}"

    // En del av cron-uttrykket er feil
    java.lang.IllegalStateException: Encountered invalid @Scheduled method 'prefetchAnsattListe': For input string: "7x19"

* Oppdatering av kodeverk for Arkivtemaer

Denne jobben oppdaterer kodeverk for Arkivtemaer ved midnatt hver dag.

* Sletting av Wicket resource cache

Wicket har en evig cache på string-resources brukt av applikasjonen, noe som gjør at enonictekster bare blir hentet ved oppstart.
Denne jobben kjøres derfor hver halvtime for å få oppdatert data fra enonic.


## Oppstart av appen på Jetty
Via StartJetty klassen kan du starte Modiabrukerdialog lokalt. Integrasjon mot andre tjenester kan konfigureres i jetty-environment.properties.
Nødvendige credentials legges i credentials.properties.
I tilegg må du legge en fasit.properties fil i din hjemmemappe med følgende innhold:
```
testmiljo=q6
domenebrukernavn=
domenepassord=

veilederident=
veilederpassord=
```
- Hvis man får OutOfMemoryError ved lokal kjøring så kan man sette opp PermGen space i prosessen som kjører StartJetty,
ved å legge til -XX:MaxPermSize=256M som VM Option.
I IntelliJ gjøres dette i ved å klikke "Edit Configurations" i nedtrekksmenyen for prosessen "StartJetty" i topplinjen.

### Testing
- Java tester kjøres hver gang man bygger Modia og subset av tester kan også kjøres i IntelliJ. I terminalen `mvn test`, evt. `mvn clean install`.
- Js-tester må køres manuellt. Kør filen test_runner.html.

### IntelliJ

- Opprett en run configuration for klassen StartJetty
- Endre working directory til $MODULE_DIR$

### Testbrukere og IDA

Lokalt trenger man ikke å logge inn med noen brukere, blankt brukernavn og passord holder. 

I T og Q-miljøer kan man bruke https://modapp-<miljø>.adeo.no/modiabrukerdialog/j_security_check for innlogging med egenvalgte brukere uten å måtte logge inn i Nav skrivebord. 

Testbrukere kan administreres i [IDA](http://ida.adeo.no/). Her kan man opprette testidenter i AD og gi kontorspesifikke rettigheter mot NORG. 
Man kan søke etter eksisterende idente runder "Identoversikten" i IDA, og søk etter xteam gi deg teamets identer og hvordan de se ut i de forskjellige miljøene.

Roller tilordnet i AD går på tvers av alle miljøer. Hvilke AD-roller som påvirker Modia finner du i [sysdok](http://confluence.adeo.no/display/EAF/Modiabrukerdialog+-+Tilgangskontroll#Modiabrukerdialog-Tilgangskontroll-Prosesserogroller).

NORG må få satt rettigheter pr miljø. Systemet bestemmer hvilket Nav-kontor (enhet) saksbehandleren er tilknyttet og hvilke rolle den har der. 
Det finnes ingen tilsvarende sysdok som forteller om roller i NORG, men `SAKB` og `OPPB_GOS` er de vanligste   

## Releasing ved brekkende endringer i Modia og bibliotek som avhenger av Modia

Hvis det gjøres endringer i Modia som avhenger av endringer i
biblioteker (f.eks. kjerneinfo) som selv har en av Modias undermoduler
(f.eks. modiabrukerdialog-api) som avhengighet, kreves det
litt arbeid for å få releaset det hele.

### I modiabrukerdialog

Release modiabrukerdialog, reactkomponenter og modiabrukerdialog-api:

```console
$ mvn versions:set -DnewVersion=<gjeldende versjon pluss ett eller annet suffiks>
$ mvn clean deploy -pl .,reactkomponenter,modiabrukerdialog-api
$ mvn versions:set -DnewVersion=dev
$ mvn versions:commit
```

### I kjerneinfo og/eller modia-felleskomponenter

- Oppdater `modiabrukerdialog.version` i pom.xml til versjonen fra forrige steg
- Sjekk at det bygger
- Push til master for å release

### I modiabrukerdialog

- Oppdater versjonen på kjerneinfo eller modia-felleskomponenter
- Sjekk at det bygger
- Push til master for å release
