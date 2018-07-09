import * as AT from './../../action-types';
import * as Const from './../../konstanter';
import { basicReducer } from './../../utils/redux-utils';

const actionHandlers = {};
const initalState = { status: Const.VOID, data: {}, feil: '' };
// ------- Your handler here

actionHandlers[AT.LAST_DOKUMENT_DATA_START] = (state) => (
    { ...state, status: Const.LASTER }
);
actionHandlers[AT.LAST_DOKUMENT_DATA_OK] = (state, action) => (
    { ...state, status: Const.LASTET, data: action.data }
);
actionHandlers[AT.LAST_DOKUMENT_DATA_FEIL] = (state, action) => (
    { ...state, status: Const.FEILET, feil: action.data }
);

// -------
export default basicReducer(initalState, actionHandlers);
