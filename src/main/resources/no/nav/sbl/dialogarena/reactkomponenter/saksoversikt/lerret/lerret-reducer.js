import * as AT from './../action-types';
import * as Const from './../konstanter';
import React from 'react';
import { FormattedMessage } from 'react-intl';
import { basicReducer } from './../utils/redux-utils';
import { nyesteSakstema } from './../utils/dato-sortering';
import { underBehandlingEllerNyereEnnGrenseverdi } from './../utils/siste-oppdatering/behandlingsstatus';

const fjernTommeTema = (antallDagerFerdigBehandletStatusErGyldig) =>
    tema =>
        tema.dokumentMetadata.length > 0
        || tema.behandlingskjeder
            .filter(underBehandlingEllerNyereEnnGrenseverdi(antallDagerFerdigBehandletStatusErGyldig))
            .length > 0;

const lagAlleTema = (temaliste) => [{
    temanavn: <FormattedMessage id="sakslamell.alletemaer" />,
    temakode: 'alle',
    behandlingskjeder: temaliste.reduce((acc, tema) =>
        acc.concat(tema.behandlingskjeder), []),
    dokumentMetadata: temaliste.reduce((acc, tema) =>
        acc.concat(tema.dokumentMetadata), []),
    harTilgang: true
}];

const actionHandlers = {};
const initalState = {
    valgtside: 'sakstema',
    filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true },
    status: Const.VOID,
    data: {},
    feil: '',
    valgtTema: null,
    valgtJournalpost: null
};
// ------- Your handler here

actionHandlers[AT.LAST_LERRET_DATA_START] = (state) => ({ ...state, status: Const.LASTER });

actionHandlers[AT.LAST_LERRET_DATA_OK] = (state, action) => {
    let status = Const.LASTET;

    const [tekster, miljovariabler, sakstema] = action.data;
    const { resultat, feilendeSystemer } = sakstema.value ? sakstema.value : { result: [], feilendeSystemer: [] };

    const feilendeKall = action.data.filter(object => object.state === 'rejected');
    if (feilendeKall.length > 0) {
        status = Const.FEILET;
    }

    let _sakstema = resultat && resultat.length > 0 ? resultat
        .filter(fjernTommeTema(parseInt(miljovariabler.value['behandlingsstatus.synlig.antallDager'], 10)))
        .sort(nyesteSakstema) : [];
    let temakodeliste = _sakstema.map(sakstema => sakstema.temakode);
    _sakstema = _sakstema.length > 1 ? lagAlleTema(_sakstema).concat(_sakstema) : _sakstema;

    const valgtTema = state.valgtTema ? state.valgtTema : _sakstema[0];
    return {
        ...state,
        status,
        valgtTema,
        temakodeliste,
        filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true },
        data: {
            sakstema: _sakstema,
            feilendeSystemer,
            tekster: tekster.value,
            miljovariabler: miljovariabler.value
        }
    };
};
actionHandlers[AT.LAST_LERRET_DATA_FEIL] = (state, action) => {
    const [tekster, miljovariabler] = action.data;

    return {
        ...state,
        status: Const.FEILET,
        data: {
            sakstema: [],
            tekster: tekster.value,
            miljovariabler: miljovariabler.value
        },
        feil: action.data
    };
};

actionHandlers[AT.VELG_SAK] = (state, action) => ({
    ...state,
    valgtTema: action.data,
    filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true }
});
actionHandlers[AT.VELG_JOURNALPOST] = (state, action) => (
    {
        ...state,
        valgtJournalpost: action.data,
        scrollToDokumentId: action.data.journalpostId
    }
);
actionHandlers[AT.VIS_SIDE] = (state, action) => ({ ...state, valgtside: action.data });
actionHandlers[AT.VELG_FILTRERING_AVSENDER] = (state, action) => ({
    ...state,
    filtreringsvalg: action.filtreringsvalg
});

actionHandlers[AT.PURGE_SCROLL_ID] = (state) => ({
    ...state,
    scrollToDokumentId: ''
});
// -------
export default basicReducer(initalState, actionHandlers);
