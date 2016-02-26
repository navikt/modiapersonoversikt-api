import React from 'react';
import { Provider } from 'react-redux';
import { store } from './../store';
import * as Actions from './../actions';

export function wrapWithProvider(Component, store) {
    const wrapperKlasse = class ProviderWrapper extends React.Component {

        callAction(action, ...args) {
            store.dispatch(Actions[action](...args));
        }

        render() {
            const {...props} = this.props;
            return (
                <Provider store={store}><Component {...props} /></Provider>
            );
        }
    };
    wrapperKlasse.displayName = `ProviderWrapper(${Component.displayName})`;

    return wrapperKlasse;
}

export function basicReducer(initalState, actionHandlers) {
    const voidHandler = (state) => state;
    return (state = initalState, action) => {
        const matchingHandler = actionHandlers[action.type] || voidHandler;
        return matchingHandler(state, action);
    }
}