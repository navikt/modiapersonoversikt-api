import PT from 'prop-types';

export const grunnInfoType = PT.shape({
    bruker: PT.shape({
        fnr: PT.string,
        fornavn: PT.string,
        etternavn: PT.string,
        navkontor: PT.string,
        kjonn: PT.string
    }),
    Saksbehandler: PT.shape({
        enhet: PT.string,
        fornavn: PT.string,
        etternavn: PT.string
    })
});

export const skrivestotteprops = PT.shape({
    autofullfor: grunnInfoType.isRequired,
    svaksynt: PT.bool,
    knagger: PT.array
});

export const meldingITraadVisning = PT.shape({
    temagruppeNavn: PT.string,
    visningsDatoTekst: PT.string,
    fritekst: PT.string,
    erDokumentMelding: PT.bool,
    id: PT.string,
    statusTekst: PT.string,
    navIdent: PT.string,
    skrevetAv: PT.string,
    fnrBruker: PT.string,
    meldingstype: PT.string,
    opprettetDato: PT.string
});
