import { combineReducers } from 'redux';
import widgetReducer from './widget/widgetReducer';
import lerretReducer from './lerret/lerretReducer';

export default combineReducers({
    widget: widgetReducer,
    lerret: lerretReducer
});
