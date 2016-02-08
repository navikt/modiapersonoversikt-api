window.ModiaJS = {
    Components: {
        MeldingerSok: require('./meldinger-sok/meldinger-sok-module'),
        Skrivestotte: require('./skrivestotte/skrivestotte-module'),
        JournalforingsPanel: require('./journalforing-panel/journalforing-panel-module'),
        VelgSakPanel: require('./velg-sak-panel/velg-sak-panel-module'),
        VarselLerret: require('./varsel-lerret/varsel-lerret-module'),
        FeilmeldingsModaler: require('./feilmeldingsmodaler/feilmeldingsmodaler-module'),
        SaksoversiktLerret: require('./saksoversikt-lerret/saksoversikt-lerret-module')
    },
    InitializedComponents: {},
    React: require('./nav-react')
};

module.exports = window.ModiaJS.Components;
