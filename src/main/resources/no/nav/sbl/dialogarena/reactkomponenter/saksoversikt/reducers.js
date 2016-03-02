import { combineReducers } from 'redux';
import widgetReducer from './widget/widgetReducer';
import lerretReducer from './lerret/lerretReducer';
import dokumentReducer from './lerret/dokumentvisning/dokumentReducer';

export default combineReducers({
    widget: widgetReducer,
    lerret: lerretReducer,
    dokument: dokumentReducer
});
