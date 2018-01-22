/* eslint no-unused-expressions:0 */
import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import Forhandsvisning from './forhandsvisning';
import { TraadMock } from '../utils/traad-utils';

describe('ForhandsvisningKomponent', () => {
    it('Skal rendre submit-knapp med riktig tekst', () => {
        const element = shallow(<Forhandsvisning
            traad={TraadMock}
            submitButtonValue="Dette er en knapp"
        />);

        expect(element.find('.velgPanel input').props().value).to.equal('Dette er en knapp');
    });
    it('Skal rendre meldinger', () => {
        const element = shallow(<Forhandsvisning
            traad={TraadMock}
        />);

        expect(element.find('Melding').length).to.equal(1);
    });
    it('Skal rendre feilmelding på submitError', () => {
        const element = shallow(<Forhandsvisning
            traad={TraadMock}
            submitErrorMessage="Det skjedde en feil"
            submitError={true}
        />);

        expect(element.find('.velgPanel p').text()).to.equal("Det skjedde en feil");
    });
});
