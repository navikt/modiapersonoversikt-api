/* eslint no-unused-expressions:0 */
import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import Skrivestotte from './skrivestotte-module';

describe('Skrivestotte Module', () => {
    it('skal rendre en tom-melding inni en modal når ingen tekster', () => {
        const stub = sinon.stub(Skrivestotte.prototype, 'componentDidMount');
        const element = shallow(<Skrivestotte />);
        stub.restore();

        element.setState({
            tekster: []
        });

        expect(element.is('Modal')).to.be.true;
        expect(element.find('.sok-visning.hidden').length).to.equal(1);
    });

    it('skal rendre tekstlistekomponenter i en scrollportal når det finnes tekster', () => {
        const stub = sinon.stub(Skrivestotte.prototype, 'componentDidMount');
        const element = shallow(<Skrivestotte />);
        stub.restore();

        element.setState({
            tekster: [{ key: '1' }, { key: '2' }, { key: '3' }]
        });

        expect(element.find('ScrollPortal').length).to.equal(1);
        expect(element.find('ScrollPortal').find('TekstListeKomponent').length).to.equal(3);
    });
});
