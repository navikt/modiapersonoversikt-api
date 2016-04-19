import ReactDOM from 'react-dom';
import { combineReducers } from 'redux';
import * as AT from './action-types';
import lerretReducer from './lerret/lerret-reducer';
import dokumentReducer from './lerret/dokumentvisning/dokument-reducer';

function purgeStateReducer(fn) {
    return (state, action) => {
        if (action.type === AT.PURGE_STATE) {
            const node = document.querySelector('[class="saksoversiktLerret-wicket unmount-react"]');
            if (node) {
                ReactDOM.unmountComponentAtNode(node);
            }
            // NB viktig at det sendes inn {} eller undefined for å få resatt state
            return fn({}, action);
        } else if (action.type === AT.UNMOUNT) {
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
    lerret: lerretReducer,
    dokument: dokumentReducer
}));
