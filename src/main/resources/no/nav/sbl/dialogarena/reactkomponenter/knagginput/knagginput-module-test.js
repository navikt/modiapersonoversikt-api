/* eslint-env mocha */
/* eslint no-unused-expressions:0 */
import './../test-config';
import { expect } from 'chai';
import React from 'react';
import assign from 'object-assign';
import KnaggInput from './knagginput-module';
const TestUtils = React.addons.TestUtils;

describe('KnaggInput', () => {
    const defaultProps = {
        'fritekst': '',
        'knagger': [],
        'placeholder': 'Søk',
        'onChange': function onChange() {
        },
        'onKeyDown': function onKeyDown() {
        },
        'aria-label': 'Søk etter skrivestøttetekster',
        'aria-controls': 'tekstListePanel',
        'auto-focus': false
    };

    it('Lager komponenten', () => {
        const props = assign({}, defaultProps);

        const knaggElement = TestUtils.renderIntoDocument(
            <KnaggInput {...props} />
        );

        const knaggcontainer = TestUtils.findRenderedDOMComponentWithClass(knaggElement, 'knagg-input');
        const knagg = TestUtils.scryRenderedDOMComponentsWithClass(knaggElement, 'knagg');

        expect(knaggcontainer).not.to.be.null;
        expect(knagg.length).to.be.eql(0);
    });

    it('Legger inn default knagger', () => {
        const props = assign({}, defaultProps);
        props.knagger.push('testknagg');

        const knaggElement = TestUtils.renderIntoDocument(
            <KnaggInput {...props} />
        );

        const knagg = TestUtils.scryRenderedDOMComponentsWithClass(knaggElement, 'knagg');

        expect(knagg.length).to.be.eql(1);
    });
});
