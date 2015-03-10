Reactkomponenter
================

## Forberedelser for å kjøre

* Installer node.js, finnes på *F:\F2990\Felles Filer\3 Forvaltningsseksjonen\3.4 Kontor for brukerdialog\Portaler og SBL forvaltning\7. Teknisk\Programmer\nodejs*. Bruk nyeste versjon.

* Kjør følgende kommandoer i en terminal, (2a, 2b, 2c, 2d fra [denne confluence siden](http://confluence.adeo.no/display/AURA/Karma)):

```
npm config set https-proxy "https://155.55.60.117:8088"
npm config set proxy "http://155.55.60.117:8088/"
npm config set registry "http://registry.npmjs.org/"
npm config set strict-ssl false
```



* Kjør `maven clean install` for å laste ned alle JS-avhengigheter og bygge JS-modulene (hvis du starter maven i en terminal, må den ha støtte for GIT).

* Alternativt, kan du kjøre `npm install && gulp`

## Utvikling

* Under utvikling kjøres `gulp dev`. Forandringer i koden vil da automatisk bli bygd inn og lagt i `target` mappen.

## Test

* For å kjøre tester bruk `mvn test`. (`mvn clean install` vil også kjøre testene)