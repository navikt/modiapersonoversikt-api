Reactkomponenter
================

## Forberedelser for å kjøre

* Installer node.js, finnes på *F:\programvare\nodejs*.

* Konfigurer npm - følgende må ligge i .npmrc:

```
https.proxy=https://155.55.60.117:8088/
proxy=http://155.55.60.117:8088/
registry=http://registry.npmjs.org/
strict-ssl=false
http.http://stash.devillo.no.proxy=
http.proxy=http://155.55.60.117.8088/
url.https://.insteadof=git://
```

* Konfigurer git - følgende må ligge i .gitconfig:

```
[user]
	name = FORNAVN ETTERNAVN
	email = INSERT_EMAIL
[http]
	proxy = http://155.55.60.117:8088/
	sslVerify = false
[http "http://stash.devillo.no"]
	proxy =

[https]
	proxy = https://155.55.60.117:8088/

[url "https://"]
	insteadOf = git://
[url "https://github.com/"]
	insteadOf = git@github.com:
```

* Kjør `mvn clean install` for å laste ned alle JS-avhengigheter og bygge JS-modulene (hvis du starter maven i en terminal, må den ha støtte for GIT).

* Alternativt og anbefalt, kan du installere gulp globalt (`npm install gulp -g`) og så kjøre `npm install && gulp` fra rotmappa (reactkomponenter).

## Utvikling

* Under utvikling kjøres `gulp dev` fra rot-katalogen (reactkomponenter). Forandringer i koden vil da automatisk bli bygd inn og lagt i `target` mappen.

* Om du får en feil i gata `Cannot read property "apply" of undefined` prøv å kjøre følgende kommando: `npm i -g gulp-cli` 

## Test

#### Kjøre tester i kommandolinje
Tester kan kjøres i kommandolinje på to måter:

1. `gulp test`
2. `mvn test`, evt. `mvn clean install`

#### Kjøre tester i intelliJ

Sett opp intelliJ

1. Installer nodeJS plugin ( `F:\programvare\idea\plugins\` )
2. Run / Edit Configurations / Defaults / Mocha / Extra Mocha options: `--require ignore-styles --compilers js:babel-core/register" `

Kjøre test

* Høreklikk / Run

## Tips

* Marker node_modules mappen som ekskludert i IntelliJ. Høyreklikk på mappen, velg "Mark Directory As" og "Excluded".

## Retningslinjer for kode

Alle filnavn og mapper skal skrives som lowercase med dash (-) som skilletegn.
Hovedfilen (eller komponenten) i en mappe skal ha samme navn som mappen den ligger i og ha "-module" postfix.
Alle require/import uttrykk skal skrives uten ".js" postfixet.
