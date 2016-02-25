import * as AT from './../actionTypes';
import * as Const from './../konstanter';
import { basicReducer } from './../utils/redux-utils';
import { finnSisteOppdatering } from './../utils/finn-siste-oppdatering';
import { nyesteSakstema } from './../utils/dato-sortering';

const fjernTommeTema = (tema) => tema.dokumentMetadata.length > 0 || tema.behandlingskjeder.length > 0;

const actionHandlers = {};
const initalState = { status: Const.VOID, data: {}, feil: '', valgtTema: null };
// ------- Your handler here

actionHandlers[AT.LAST_LERRET_DATA_START] = (state) => {
    return { ...state, status: Const.LASTER };
};
actionHandlers[AT.LAST_LERRET_DATA_OK] = (state, action) => {
    const [temaer, sakstema, tekster, miljovariabler, fnr] = action.data;

    const _sakstema = sakstema
        .filter(fjernTommeTema).sort(nyesteSakstema);

    return {
        ...state,
        status: Const.LASTET,
        data: {
            temaer,
            sakstema: _sakstema,
            tekster,
            miljovariabler,
            fnr
        }
    };
};
actionHandlers[AT.LAST_LERRET_DATA_FEIL] = (state, action) => {
    return { ...state, status: Const.FEILET, feil: action.data };
};

actionHandlers[AT.VELG_SAK] = (state, action) => {
    return { ...state, valgtTema: action.data };
};


// -------
export default basicReducer(initalState, actionHandlers);
