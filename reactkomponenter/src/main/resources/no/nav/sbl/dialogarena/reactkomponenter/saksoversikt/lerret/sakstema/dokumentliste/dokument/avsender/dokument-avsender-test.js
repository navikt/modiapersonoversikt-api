/* eslint-env mocha */
import './../../../../../../test-config';
import React from 'react';
import { expect } from 'chai';
import DokumentAvsender from './dokument-avsender';
import { shallow } from 'enzyme';

const mockProps = {
    mottaker: '',
    kategoriNotat: ''
};

describe('DokumentAvsender', () => {
    it('Viser inngaaende hvis retningen er inn', () => {
        const props = {
            ...mockProps,
            retning: 'INN',
            avsender: 'SLUTTBRUKER',
            navn: 'Test testesen'
        };

        const result = shallow(<DokumentAvsender {...props} />);

        expect(result.is('Inngaaende')).to.be.true;
    });

    it('Viser utgaaende hvis retningen er ut', () => {
        const props = {
            ...mockProps,
            retning: 'UT',
            avsender: 'NAV',
            navn: 'NAV'
        };

        const result = shallow(<DokumentAvsender {...props} />);

        expect(result.is('Utgaaende')).to.be.true;
    });

    it('Viser intern hvis retningen er intern', () => {
        const props = {
            ...mockProps,
            retning: 'INTERN',
            avsender: 'NAV',
            navn: null
        };

        const result = shallow(<DokumentAvsender {...props} />);

        expect(result.is('Intern')).to.be.true;
    });

    it('Viser ukjent hvis retningen er udefinert', () => {
        const props = {
            ...mockProps,
            retning: 'NOE_ANNET',
            avsender: 'NAV',
            navn: null
        };

        const result = shallow(<DokumentAvsender {...props} />);

        expect(result.is('span')).to.be.true;
    });
});
