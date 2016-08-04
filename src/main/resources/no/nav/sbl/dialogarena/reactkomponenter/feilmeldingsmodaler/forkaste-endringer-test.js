import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import ForkasteEndringer from './forkaste-endringer';

describe('Forkaste endringer', () => {
    it('skal rendre informasjon inni en modal', () => {
        const element = shallow(<ForkasteEndringer
          beskrivendeTekst="test-beskrivende"
          avbryttekst="test-avbryt"
          fortsetttekst="test-fortsett"
          title="test-tittel"
        />);

        expect(element.is('Modal')).to.be.true;
        expect(element.prop('title')).to.equal('test-tittel');

        expect(element.find('section').text()).to.contain('test-beskrivende');
        expect(element.find('button').text()).to.equal('test-avbryt');
        expect(element.find('a').text()).to.equal('test-fortsett');
    });

    it('avbrytknapp skal sende discard til wicket', () => {
        const discardCallback = () => undefined;
        const element = shallow(<ForkasteEndringer discardCallback={discardCallback} />);

        const wicketSpy = sinon.spy();
        element.instance().sendToWicket = wicketSpy;

        element.find('button').simulate('click');

        expect(wicketSpy).to.have.been.calledWith(discardCallback);
    });

    it('fortsett skal sende confirm til wicket', () => {
        const confirmCallback = () => undefined;
        const element = shallow(<ForkasteEndringer confirmCallback={confirmCallback} />);

        const wicketSpy = sinon.spy();
        element.instance().sendToWicket = wicketSpy;

        element.find('a').simulate('click');

        expect(wicketSpy).to.have.been.calledWith(confirmCallback);
    });
});
