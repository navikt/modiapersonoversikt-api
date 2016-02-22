import { combineReducers } from 'redux';
import temaReducer from './temaReducers';

export default combineReducers({
    temaer: temaReducer
});
