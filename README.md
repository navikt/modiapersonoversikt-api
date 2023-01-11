# Modia brukerdialog

Modia er en intern arbeidsflate som inneholder kjerneinformasjon om brukeren og som gir medarbeiderne i NAV oversikt over brukerens forhold til NAV.

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

[Team Personoversikt](https://github.com/navikt/info-team-personoversikt)

## Dokumentasjon

Noen nyttige lenker og tips ligger på følgende lenke: https://confluence.adeo.no/pages/viewpage.action?pageId=272512650

### Caching

Cacheoppsett er hovedsakelig beskrevet ved bruk av annotasjon og benytter seg av Springs cache abstraction og en underliggende caffeina-cache-implementasjon.

Tjenestekall mot NORG caches i to forskjellige cacher. Se hver enkelt tjeneste for eksakt hvilke kall som bruker hvilken cache.
Det er hovedsakelig en forskjell på time-to-live og key-generering. Den ene cachen har caching som tar hensyn til innlogget saksbehandler, og har en kort time-to-live.
Den andre cachen har data som ikke endres pr bruker og caches derfor per node. Denne har også en lengre time-to-live for at dataene skal leve frem til neste faste
cache-populeringsjobb. Se *Faste jobber* for mer informasjon om denne.

Tjenestekall mot andre tjenester har egne cacher.

Cacheoppsettet er ikke blocking, dvs at dersom to tråder spør med like keys vil cachen slippe gjennom to kall til den underliggende tjenesten.

### Faste jobber

* Oppdatering av kodeverk for Arkivtemaer

Denne jobben oppdaterer kodeverk for Arkivtemaer ved midnatt hver dag.


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
