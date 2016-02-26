import * as AT from './../actionTypes';
import * as Const from './../konstanter';
import React from 'react';
import { FormattedMessage } from 'react-intl';
import { basicReducer } from './../utils/redux-utils';
import { finnSisteOppdatering } from './../utils/finn-siste-oppdatering';
import { nyesteSakstema } from './../utils/dato-sortering';

const fjernTommeTema = (tema) => tema.dokumentMetadata.length > 0 || tema.behandlingskjeder.length > 0;
const lagAlleTema = (temaliste) => [{
    temanavn: <FormattedMessage id="sakslamell.alletemaer"/>,
    temakode: 'alle',
    behandlingskjeder: temaliste[0].behandlingskjeder,
    dokumentMetadata: temaliste[0].dokumentMetadata
}];

const actionHandlers = {};
const initalState = {
    valgtside: 'sakstema',
    status: Const.VOID,
    data: {},
    feil: '',
    valgtTema: null,
    widgetValgtTemakode: null
};
// ------- Your handler here

actionHandlers[AT.LAST_LERRET_DATA_START] = (state) => {
    return {...state, status: Const.LASTER};
};
actionHandlers[AT.LAST_LERRET_DATA_OK] = (state, action) => {
    const [temaer, sakstema, tekster, miljovariabler, fnr] = action.data;

    let _sakstema = sakstema
        .filter(fjernTommeTema).sort(nyesteSakstema);
    _sakstema = _sakstema.length > 1 ? lagAlleTema(_sakstema).concat(_sakstema) : _sakstema;

    let { valgtTema, widgetValgtTemakode } = state;

    if (state.widgetValgtTemakode !== null) {
        valgtTema = _sakstema.find((tema) => tema.temakode === widgetValgtTemakode);
        widgetValgtTemakode = null;
    } else {
        valgtTema = _sakstema[0];
    }

    return {
        ...state,
        status: Const.LASTET,
        data: {
            temaer,
            sakstema: _sakstema,
            tekster,
            miljovariabler,
            fnr
        },
        valgtTema,
        widgetValgtTemakode
    };
};
actionHandlers[AT.LAST_LERRET_DATA_FEIL] = (state, action) => {
    return {...state, status: Const.FEILET, feil: action.data};
};

actionHandlers[AT.VELG_SAK] = (state, action) => ({...state, valgtTema: action.data});
actionHandlers[AT.VIS_TEMA] = (state, action) => ({...state, widgetValgtTemakode: action.data});
actionHandlers[AT.VIS_SIDE] = (state, action) => ({...state, valgtside: action.data});


// -------
export default basicReducer(initalState, actionHandlers);
