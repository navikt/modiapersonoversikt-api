import React from 'react';
import { Provider } from 'react-redux';
import { store } from './../store';

export function wrapWithProvider(Component, store) {
    return (props) => <Provider store={store}><Component {...props} /></Provider>;
}

export function basicReducer(initalState, actionHandlers) {
    const voidHandler = (state) => state;
    return (state = initalState, action) => {
        const matchingHandler = actionHandlers[action.type] || voidHandler;
        return matchingHandler(state, action);
    }
}