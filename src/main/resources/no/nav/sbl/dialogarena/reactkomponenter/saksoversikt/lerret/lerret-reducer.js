import * as AT from './../action-types';
import * as Const from './../konstanter';
import React from 'react';
import { FormattedMessage } from 'react-intl';
import { basicReducer } from './../utils/redux-utils';
import { nyesteSakstema } from './../utils/dato-sortering';

const fjernTommeTema = (tema) => tema.dokumentMetadata.length > 0 || tema.behandlingskjeder.length > 0;
const lagAlleTema = (temaliste) => [{
    temanavn: <FormattedMessage id="sakslamell.alletemaer"/>,
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
    valgtJournalpost: null,
    widgetValgtTemakode: null
};
// ------- Your handler here

actionHandlers[AT.LAST_LERRET_DATA_START] = (state) => ({ ...state, status: Const.LASTER });

actionHandlers[AT.LAST_LERRET_DATA_OK] = (state, action) => {
    const [tekster, miljovariabler, sakstema] = action.data;
    const { resultat, feilendeSystemer } = sakstema.value;

    let _sakstema = resultat && resultat.length > 0 ? resultat
        .filter(fjernTommeTema).sort(nyesteSakstema) : [];
    _sakstema = _sakstema.length > 1 ? lagAlleTema(_sakstema).concat(_sakstema) : _sakstema;

    let { valgtTema, widgetValgtTemakode } = state;

    if (state.widgetValgtTemakode !== null) {
        valgtTema = _sakstema.find((tema) => tema.temakode === widgetValgtTemakode);
        widgetValgtTemakode = null;
    } else if (valgtTema === null) {
        valgtTema = _sakstema[0];
    }

    return {
        ...state,
        status: Const.LASTET,
        valgtTema,
        widgetValgtTemakode,
        filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true },
        data: {
            sakstema: _sakstema,
            feilendeSystemer,
            tekster: tekster.value,
            miljovariabler: miljovariabler.value
        }
    };
};
actionHandlers[AT.LAST_LERRET_DATA_FEIL] = (state, action) => ({ ...state, status: Const.FEILET, feil: action.data });

actionHandlers[AT.VELG_SAK] = (state, action) => ({
    ...state,
    valgtTema: action.data,
    filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true }
});
actionHandlers[AT.VELG_JOURNALPOST] = (state, action) => ({ ...state, valgtJournalpost: action.data });
actionHandlers[AT.VIS_TEMA] = (state, action) => ({ ...state, widgetValgtTemakode: action.data });
actionHandlers[AT.VIS_SIDE] = (state, action) => ({ ...state, valgtside: action.data });
actionHandlers[AT.VELG_FILTRERING_AVSENDER] = (state, action) => ({
    ...state,
    filtreringsvalg: action.filtreringsvalg
});

// -------
export default basicReducer(initalState, actionHandlers);
