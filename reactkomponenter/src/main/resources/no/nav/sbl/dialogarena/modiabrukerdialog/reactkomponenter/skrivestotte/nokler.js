

export function getVerdi(autofullforMap, nokkel, valgtLocale){
    const nokler = {
        'bruker.fnr': autofullforMap.bruker.fnr,
        'bruker.fornavn': autofullforMap.bruker.fornavn,
        'bruker.etternavn': autofullforMap.bruker.etternavn,
        'bruker.navn': autofullforMap.bruker.navn,
        'bruker.navkontor': autofullforMap.bruker.navkontor,
        'bruker.subjekt': subjektPronomen(autofullforMap.bruker.kjonn, valgtLocale),
        'bruker.objekt': objektPronomen(autofullforMap.bruker.kjonn, valgtLocale),
        'saksbehandler.fornavn': autofullforMap.saksbehandler.fornavn,
        'saksbehandler.etternavn': autofullforMap.saksbehandler.etternavn,
        'saksbehandler.navn': autofullforMap.saksbehandler.navn,
        'saksbehandler.enhet': autofullforMap.saksbehandler.enhet
    };

    const verdi = nokler[nokkel];
    if (!verdi) {
        return '[ukjent nøkkel]';
    }
    return verdi;
}

function objektPronomen(kjonn, valgtLocale) {
    switch (kjonn) {
        case 'K':
            switch (valgtLocale) {
                case 'nb_NO':
                    return 'henne';
                case 'nn_NO':
                    return 'ho';
                case 'en_US':
                    return 'her';
                default:
                    return null;

            }
        case 'M':
            switch (valgtLocale) {
                case 'nb_NO':
                    return 'ham';
                case 'nn_NO':
                    return 'han';
                case 'en_US':
                    return 'him';
                default:
                    return null;
            }
        default:
            return null;
    }
}

function subjektPronomen(kjonn, valgtLocale) {
    switch (kjonn) {
        case 'K':
            switch (valgtLocale) {
                case 'nb_NO':
                    return 'hun';
                case 'nn_NO':
                    return 'ho';
                case 'en_US':
                    return 'she';
                default:
                    return null;

            }
        case 'M':
            switch (valgtLocale) {
                case 'nb_NO':
                    return 'han';
                case 'nn_NO':
                    return 'han';
                case 'en_US':
                    return 'he';
                default:
                    return null;
            }
        default:
            return null;
    }
}