import './../test-config';
import { expect } from 'chai';
import React from 'react/addons';
import assign from 'object-assign';
import KnaggInput from './knagginput-module';
const TestUtils = React.addons.TestUtils;

describe('KnaggInput', function () {
    const defaultProps = {
        'fritekst': '',
        'knagger': [],
        'placeholder': 'Søk',
        'onChange': function () {
        },
        'onKeyDown': function () {
        },
        'aria-label': 'Søk etter skrivestøttetekster',
        'aria-controls': 'tekstListePanel',
        'auto-focus': false
    };

    it('Lager komponenten', function () {
        const TestUtils = React.addons.TestUtils;

        const props = assign({}, defaultProps);

        const knaggElement = TestUtils.renderIntoDocument(
            <KnaggInput {...props} />
        );

        const knaggcontainer = TestUtils.findRenderedDOMComponentWithClass(knaggElement, 'knagg-input');
        const knagg = TestUtils.scryRenderedDOMComponentsWithClass(knaggElement, 'knagg');

        expect(knaggcontainer).not.to.be.null;
        expect(knagg.length).to.be.eql(0);
    });

    it('Legger inn default knagger', function () {
        const props = assign({}, defaultProps);
        props.knagger.push('testknagg');

        const knaggElement = TestUtils.renderIntoDocument(
            <KnaggInput {...props} />
        );

        const knagg = TestUtils.scryRenderedDOMComponentsWithClass(knaggElement, 'knagg');

        expect(knagg.length).to.be.eql(1);
    });
});