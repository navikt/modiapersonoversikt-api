import './../test-config';
import { expect } from 'chai';
import React from 'react/addons';
import LocaleSelect from './locale-select';
const TestUtils = React.addons.TestUtils;

describe('LocaleSelect', function () {
    it('rendrer ingenting hvis kun ett locale', function () {
        const props = {tekst: {innhold: [{nb_NO: 'tekst'}]}};

        const element = TestUtils.renderIntoDocument(
            <LocaleSelect {...props} />
        );

        const rendered = TestUtils.scryRenderedDOMComponentsWithTag(element, 'select');

        expect(rendered).to.be.length(0);
    });

    it('rendrer selectboks med locales hvis mer enn ett locale', function () {
        const props = {tekst: {innhold: [{nb_NO: 'tekst'}, {en_US: 'text'}]}};

        const element = TestUtils.renderIntoDocument(
            <LocaleSelect {...props} />
        );

        const rendered = TestUtils.scryRenderedDOMComponentsWithTag(element, 'select');

        expect(rendered).to.be.length(1);
    });
});