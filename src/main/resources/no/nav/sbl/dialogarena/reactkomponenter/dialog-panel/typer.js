import PT from 'prop-types';

export const grunnInfoType = PT.shape({
    bruker: PT.shape({
        fnr: PT.string,
        fornavn: PT.string,
        etternavn: PT.string,
        navkontor: PT.string
    }),
    Saksbehandler: PT.shape({
        enhet: PT.string,
        fornavn: PT.string,
        etternavn: PT.string
    }
    )
});
