/* eslint no-unused-expressions:0 */
import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import OkonomiskSosialhjelpKnapp from './okonomisk-sosialhjelp-knapp';

describe('OkonomiskSosialhjelpKnapp', () => {
    const dummyEvent = {
        preventDefault() {}
    };

    it('rendrer en ekspandert knapp som kan lukkes by default', () => {
        const element = shallow(<OkonomiskSosialhjelpKnapp />);

        expect(element.find('.ekspanderingspil.opp').length).to.equal(1);
        expect(element.find('.list-saker').length).to.equal(1);
    });

    it('å trykke på en ekspandert knapp lukker den', () => {
        const element = shallow(<OkonomiskSosialhjelpKnapp />);

        element.find('button').simulate('click', dummyEvent);

        expect(element.find('.ekspanderingspil.ned').length).to.equal(1);
        expect(element.find('.list-saker').length).to.equal(0);
    });

    it('å velge økonomisk sosialhjelp kaller velg sak', () => {
        const velgsakSpy = sinon.spy();
        const element = shallow(<OkonomiskSosialhjelpKnapp velgSak={velgsakSpy} />);

        element.find('a').simulate('click', dummyEvent);

        expect(velgsakSpy).to.have.been.called;
    });
});
