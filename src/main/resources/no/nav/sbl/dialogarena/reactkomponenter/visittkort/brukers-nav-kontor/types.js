import PT from 'prop-types';

export const OrganiasjonType = {
    organisasjon: PT.shape({
        enhetId: PT.string.isRequired,
        enhetNavn: PT.string.isRequired,
        kontaktinformasjon: PT.shape({
            publikumsmottak: PT.arrayOf(PT.shape({
                besoeksadresse: PT.shape({
                    gatenavn: PT.string.isRequired
                }).isRequired
            })).isRequired
        }).isRequired
    })
};
