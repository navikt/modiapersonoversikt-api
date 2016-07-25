import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import Skrivestotte from './skrivestotte-module';

describe('Skrivestotte Module', () => {
    it('skal rendre en tom-melding inni en modal når ingen tekster', () => {
        const element = shallow(<Skrivestotte />);

        element.setState({
            tekster: []
        });

        expect(element.is('Modal')).to.be.true;
        expect(element.find('.sok-visning.hidden').length).to.equal(1);
    });

    it('skal rendre tekstlistekomponenter i en scrollportal når det finnes tekster', () => {
        const element = shallow(<Skrivestotte />);

        element.setState({
            tekster: [{}, {}, {}]
        });

        expect(element.find('ScrollPortal').length).to.equal(1);
        expect(element.find('ScrollPortal').find('TekstListeKomponent').length).to.equal(3);
    });
});
