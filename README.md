Reactkomponenter
================

## Forberedelser for å kjøre

* Installer node.js, finnes på *F:\F2990\Felles Filer\3 Forvaltningsseksjonen\3.4 Kontor for brukerdialog\Portaler og SBL forvaltning\7. Teknisk\Programmer\nodejs*. Bruk nyeste versjon.

* Kjør følgende kommandoer i en terminal:

```
npm config set https-proxy "https://155.55.60.117:8088"
npm config set proxy "http://155.55.60.117:8088/"
npm config set registry "http://registry.npmjs.org/"
npm config set strict-ssl false
```

* Kjør `maven clean install` for å laste ned alle JS-avhengigheter og bygge JS-modulene (hvis du starter maven i en terminal, må den ha støtte for GIT).

## Utvikling

* Under utvikling kjøres `gulp dev` fra terminalen. Forandringer i koden vil da automatisk bli bygd inn og lagt i `target` mappen.