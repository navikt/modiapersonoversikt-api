import PT from 'prop-types';

export const besoeksadresseType = PT.shape({
    gatenavn: PT.string.isRequired,
    husnummer: PT.string,
    postnummer: PT.string.isRequired,
    poststed: PT.string.isRequired,
    husbokstav: PT.string
});

export const apningstidType = PT.shape({
    ukedag: PT.string.isRequired,
    apentFra: PT.shape({
        time: PT.string.isRequired,
        minutt: PT.string.isRequired,
        sekund: PT.string.isRequired
    }).isRequired,
    apentTil: PT.shape({
        time: PT.string.isRequired,
        minutt: PT.string.isRequired,
        sekund: PT.string.isRequired
    }).isRequired
});

export const apningstiderType = PT.shape({
    apningstider: PT.arrayOf(apningstidType).isRequired
});

export const publikumsmottakType = PT.shape({
    besoeksadresse: besoeksadresseType.isRequired,
    apningstider: apningstiderType.isRequired
});

export const organisasjonType = PT.shape({
    enhetId: PT.string.isRequired,
    enhetNavn: PT.string.isRequired,
    kontaktinformasjon: PT.shape({
        publikumsmottak: PT.arrayOf(publikumsmottakType).isRequired
    })
});
