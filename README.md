# Modia brukerdialog

## Oppstart av appen på Jetty
- Hvis man får OutOfMemoryError ved lokal kjøring så kan man sette opp PermGen space i prosessen som kjører StartJetty,
ved å legge til -XX:MaxPermSize=256M som VM Option.
I IntelliJ gjøres dette i ved å klikke "Edit Configurations" i nedtrekksmenyen for prosessen "StartJetty" i topplinjen.

### IntelliJ

- Opprett en run configuration for klassen StartJetty
- Endre working directory til $MODULE_DIR$

### Konfigurering av mockdata
http://localhost:8083/modiabrukerdialog/internal/mocksetup