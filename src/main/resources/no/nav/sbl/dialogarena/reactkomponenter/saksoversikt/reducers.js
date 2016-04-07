import ReactDOM from 'react-dom';
import { combineReducers } from 'redux';
import * as AT from './action-types';
import widgetReducer from './widget/widget-reducer';
import lerretReducer from './lerret/lerret-reducer';
import dokumentReducer from './lerret/dokumentvisning/dokument-reducer';

function purgeStateReducer(fn) {
    return (state, action) => {
        if (action.type === AT.PURGE_STATE) {
            const node = document.querySelector('[class="saksoversiktLerret-wicket unmount-react"]');
            if (node) {
                ReactDOM.unmountComponentAtNode(node);
            }
            return fn({widget:state.widget}, action);
        } else if(action.type === AT.UNMOUNT) {
            const node = document.querySelector('[class="saksoversiktLerret-wicket unmount-react"]');
            if (node) {
                ReactDOM.unmountComponentAtNode(node);
            }
            return fn(state, action);
        }
        return fn(state, action);
    };
}


export default purgeStateReducer(combineReducers({
    widget: widgetReducer,
    lerret: lerretReducer,
    dokument: dokumentReducer
}));
