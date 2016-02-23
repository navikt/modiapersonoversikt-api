/* eslint-env mocha */
import './../test-config';
import { expect } from 'chai';
import React from 'react';
import LocaleSelect from './locale-select';
import TestUtils from 'react-addons-test-utils';

describe('LocaleSelect', () => {
    it('rendrer ingenting hvis kun ett locale', () => {
        const props = {tekst: {innhold: [{nb_NO: 'tekst'}]}};

        const element = TestUtils.renderIntoDocument(
            <LocaleSelect {...props} />
        );

        const rendered = TestUtils.scryRenderedDOMComponentsWithTag(element, 'select');

        expect(rendered).to.be.length(0);
    });

    it('rendrer selectboks med locales hvis mer enn ett locale', () => {
        const props = {tekst: {innhold: [{nb_NO: 'tekst'}, {en_US: 'text'}]}};

        const element = TestUtils.renderIntoDocument(
            <LocaleSelect {...props} />
        );

        const rendered = TestUtils.scryRenderedDOMComponentsWithTag(element, 'select');

        expect(rendered).to.be.length(1);
    });
});
