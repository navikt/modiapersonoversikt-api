import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';
import ResourceMap from '../utils/resource-map';

import VarselRad from './varsel-rad';

describe('VarselRad', () => {
    let store, varsel;

    beforeEach(() => {
        const resources = new ResourceMap({});
        store = {
            toggleEkspandert: sinon.spy(),
            getResources: () => resources
        };
        varsel = {
            mottattTidspunkt: new Date(2016, 0, 1),
            meldingListe: [{ kanal: 'kanal1' }, { kanal: 'kanal2' }]
        };
    });

    it('rendrer et varsel, kollapset by default', () => {
        const element = shallow(<VarselRad store={store} varsel={varsel} />);

        expect(element.find('.header-kanal').text()).to.contain('kanal1, kanal2');
        expect(element.find('.header-dato').text()).to.contain('01. jan., 00.00');
        expect(element.find('.ekspanderingspil.ned').length).to.equal(1);
        expect(element.find('VarselRadElement').length).to.equal(0);
    });

    it('kaller ekspandert i storen ved klikk', () => {
        const element = shallow(<VarselRad store={store} varsel={varsel} />);

        element.find('button').simulate('click');

        expect(store.toggleEkspandert).to.have.been.called;
    });

    it('ekspandert rendres meldingliste-elementer', () => {
        varsel.ekspandert = true;
        const element = shallow(<VarselRad store={store} varsel={varsel} />);

        expect(element.find('VarselRadElement').length).to.equal(2);
        expect(element.find('.ekspanderingspil.opp').length).to.equal(1);
    });
});
