import * as AT from './../actionTypes';
import * as Const from './../konstanter';
import { basicReducer } from './../utils/redux-utils';
import { finnSisteOppdatering } from './../utils/finn-siste-oppdatering';

const fjernTommeTema = (tema) => tema.dokumentMetadata.length > 0 || tema.behandlingskjeder.length > 0;

const actionHandlers = {};
const initalState = { status: Const.VOID, data: {}, feil: '', valgtTema: null };
// ------- Your handler here

actionHandlers[AT.LAST_LERRET_DATA_START] = (state) => {
    return { ...state, status: Const.LASTER };
};
actionHandlers[AT.LAST_LERRET_DATA_OK] = (state, action) => {
    const [temaer, journalposter, sakstema, tekster, miljovariabler] = action.data;

    const _sakstema = sakstema
        .filter(fjernTommeTema)
        .map((tema) => ({
            temakode: tema.temakode,
            dokumentmetadata: tema.dokumentMetadata,
            temanavn: tema.temanavn,
            sistOppdatertDato: finnSisteOppdatering(tema.behandlingskjeder, tema.dokumentMetadata),
            dokumentMetadata: tema.dokumentMetadata
        }));

    return {
        ...state,
        status: Const.LASTET,
        data: {
            temaer,
            _sakstema,
            journalposter,
            tekster,
            miljovariabler
        }
    };
};
actionHandlers[AT.LAST_LERRET_DATA_FEIL] = (state, action) => {
    return { ...state, status: Const.FEILET, feil: action.data };
};

// -------
export default basicReducer(initalState, actionHandlers);
