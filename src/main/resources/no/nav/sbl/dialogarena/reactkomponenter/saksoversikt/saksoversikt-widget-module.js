import React from 'react';
import { Provider } from 'react-redux';
import { store } from './store';

import Temaliste from './temaliste';

function SaksoversiktWidget({ fnr }) {
    return (
        <Provider store={store}>
            <Temaliste fnr={fnr}/>
        </Provider>
    );
}

SaksoversiktWidget.propTypes = {
    fnr: React.PropTypes.string.isRequired
};

export default SaksoversiktWidget;

