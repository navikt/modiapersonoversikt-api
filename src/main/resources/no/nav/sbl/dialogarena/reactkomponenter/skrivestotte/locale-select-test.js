/* eslint-env mocha */
import './../test-config';
import { expect } from 'chai';
import React from 'react';
import LocaleSelect from './locale-select';
import { shallow } from 'enzyme';

describe('LocaleSelect', () => {
    it('rendrer ingenting hvis kun ett locale', () => {
        const props = { store: {}, tekst: { innhold: [{ nb_NO: 'tekst' }] } };

        const element = shallow(<LocaleSelect {...props} />);

        const rendered = element.find('select');

        expect(rendered).to.be.length(0);
    });

    it('rendrer selectboks med locales hvis mer enn ett locale', () => {
        const props = { store: {}, tekst: { innhold: [{ nb_NO: 'tekst' }, { en_US: 'text' }] } };

        const element = shallow(<LocaleSelect {...props} />);

        const rendered = element.find('select');

        expect(rendered).to.be.length(1);
    });
});
