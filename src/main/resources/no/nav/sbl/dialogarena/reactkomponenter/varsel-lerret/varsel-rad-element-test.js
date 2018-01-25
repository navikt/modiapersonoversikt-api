import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import ResourceMap from '../utils/resource-map';
import VarselRadElement from './varsel-rad-element';

describe('VarselRadElement', () => {
    let store;
    let melding;

    beforeEach(() => {
        const resources = new ResourceMap({});
        store = {
            getResources: () => resources
        };
        melding = {
            kanal: 'EPOST',
            epostemne: 'test-emne',
            innhold: 'test-innhold',
            mottakerInformasjon: ''
        };
    });

    it('rendrer et varsel element', () => {
        const element = shallow(<VarselRadElement store={store} melding={melding} />);

        expect(element.find('.innhold-melding').text()).to.contain('test-innhold');
        expect(element.find('.innhold-epostemne').text()).to.contain('test-emne');
        expect(element.find('.innhold-informasjon.ok').length).to.equal(0);
    });
});
