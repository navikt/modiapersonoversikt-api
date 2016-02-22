import * as AT from './actionTypes';
import * as Const from './konstanter';

const actionHandlers = {};
const voidHandler = (state) => state;
// ------- Your handler here

actionHandlers[AT.LAST_DATA_START] = (state) => {
    return { ...state, status: Const.LASTER };
};
actionHandlers[AT.LAST_DATA_OK] = (state, action) => {
    return { ...state, status: Const.LASTET, data: action.data };
};
actionHandlers[AT.LAST_DATA_FEIL] = (state, action) => {
    return { ...state, status: Const.FEILET, feil: action.data };
};

// -------
export default (state = { status: Const.VOID, data: {}, feil: '' }, action) => {
    const matchingHandler = action[action.type] || voidHandler;

    return matchingHandler(state, action);
};
