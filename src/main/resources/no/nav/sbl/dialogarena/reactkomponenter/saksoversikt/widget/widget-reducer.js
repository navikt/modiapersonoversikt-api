import * as AT from './../action-types';
import * as Const from './../konstanter';
import { basicReducer } from './../utils/redux-utils';

const actionHandlers = {};
const initalState = { status: Const.VOID, data: {}, feil: '' };
// ------- Your handler here

actionHandlers[AT.LAST_WIDGET_DATA_START] = (state) => {
    return { ...state, status: Const.LASTER };
};
actionHandlers[AT.LAST_WIDGET_DATA_OK] = (state, action) => {
    const temaer = action.data;
    return {
        ...state,
        status: Const.LASTET,
        data: {
            temaer,
            tekster: state.data.tekster
        }
    };
};
actionHandlers[AT.LAST_WIDGET_DATA_TEKSTER_OK] = (state, action) => {
    const tekster = action.data;
    return {
        ...state,
        status: Const.LASTER,
        data: {
            tekster
        }
    };
};
actionHandlers[AT.LAST_WIDGET_DATA_FEIL] = (state, action) => {
    return { ...state, status: Const.FEILET, feil: action.data };
};

// -------
export default basicReducer(initalState, actionHandlers);
