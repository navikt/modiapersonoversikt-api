var Utils = {
    Constants: {
        LOCALE_DEFAULT: 'nb_NO'
    },
    getInnhold: function (valgtTekst, valgtLocale) {
        if (!valgtTekst || !valgtTekst.hasOwnProperty('innhold')) {
            return '';
        }
        return valgtTekst.innhold[valgtLocale] ? valgtTekst.innhold[valgtLocale] : valgtTekst.innhold[Utils.Constants.LOCALE_DEFAULT];
    }
};



module.exports = Utils;
