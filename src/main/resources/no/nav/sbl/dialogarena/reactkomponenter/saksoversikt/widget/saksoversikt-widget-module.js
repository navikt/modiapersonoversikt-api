import React from 'react';
import { wrapWithProvider } from './../utils/redux-utils';
import { store } from './../store';
import Temaliste from './temaliste';

const SaksoversiktWidget = wrapWithProvider(Temaliste, store);

SaksoversiktWidget.propTypes = {
    fnr: React.PropTypes.string.isRequired
};

export default wrapWithProvider(SaksoversiktWidget, store);
