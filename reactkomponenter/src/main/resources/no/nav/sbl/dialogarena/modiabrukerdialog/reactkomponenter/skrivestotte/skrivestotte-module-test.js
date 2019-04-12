/* eslint no-unused-expressions:0 */
import './../test-config';
import React from 'react';
import { shallow, mount } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';
import Skrivestotte from './skrivestotte-module';
import Modal from '../modal/modal-module';


Modal.prototype.render = function() {
    return this.props.children;
};


describe('Skrivestotte Module', () => {

    let componentDidMountStub;

    beforeEach(() => {
        componentDidMountStub = sinon.stub(Skrivestotte.prototype, 'componentDidMount');
    });

    afterEach(() => {
        componentDidMountStub.restore();
    });

    it('skal rendre i en modal', () => {
        const element = shallow(<Skrivestotte />);

        expect(element.is('Modal')).to.be.true;
    });

    it('skal rendre en tom-melding når ingen tekster', () => {
        const element = mount(<Skrivestotte />);

        element.setState({
            tekster: []
        });

        expect(element.find('form.sok-layout').length).to.equal(1);
        expect(element.find('.sok-visning.hidden').length).to.equal(1);
    });

    it('skal rendre tekstlistekomponenter i en scrollportal når det finnes tekster', () => {
        const element = shallow(<Skrivestotte />);

        element.setState({
            tekster: [{ key: '1' }, { key: '2' }, { key: '3' }]
        });

        expect(element.find('ScrollPortal').length).to.equal(1);
        expect(element.find('ScrollPortal').find('TekstListeKomponent').length).to.equal(3);
    });
});
