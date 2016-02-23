import React from 'react';
import { Provider } from 'react-redux';
import { store } from './store';

export function wrapWithProvider(Component, store) {
    return (props) => <Provider store={store}><Component {...props} /></Provider>;
}