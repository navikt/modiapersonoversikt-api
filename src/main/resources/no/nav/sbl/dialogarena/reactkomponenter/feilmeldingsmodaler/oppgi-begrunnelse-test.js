import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import OppgiBegrunnelse from './oppgi-begrunnelse';

describe('Oppgi begrunnelse', () => {
    it('skal rendre et tekstfelt med lagre og avbryt inni en modal', () => {
        const element = shallow(<OppgiBegrunnelse lagretekst="testlagre" avbryttekst="testavbryt" />);

        expect(element.is('Modal')).to.be.true;
        expect(element.find('textarea').length).to.equal(1);
        expect(element.find('button.knapp-hoved').text()).to.equal('testlagre');
        expect(element.find('button.knapp-lenke').text()).to.equal('testavbryt');
    });

    it('skal vise feilmelding', () => {
        const element = shallow(<OppgiBegrunnelse lagretekst="testlagre" avbryttekst="testavbryt" />);

        element.setState({ showFeilmelding: true });

        expect(element.find('.feedbacklabel').length).to.equal(1);
    });

    it('avbrytknapp skal fjerne feilmelding og si ifra til wicket', () => {
        const discardCallback = () => undefined;
        const element = shallow(<OppgiBegrunnelse discardCallback={discardCallback} />);

        const wicketSpy = sinon.spy();
        element.instance().sendToWicket = wicketSpy;
        element.setState({ showFeilmelding: true });

        element.find('button.knapp-lenke').simulate('click', {});

        expect(wicketSpy).to.have.been.calledWith(discardCallback);
        expect(element.find('.feedbacklabel').length).to.equal(0);
    });

    it('vis og skjul skal trigge metoder på modalen', () => {
        const element = shallow(<OppgiBegrunnelse />);

        const visSpy = sinon.spy();
        const skjulSpy = sinon.spy();

        element.instance().refs = {
            modal: {
                open: visSpy,
                close: skjulSpy
            }
        };

        element.instance().vis();
        expect(visSpy).to.have.been.called;

        element.instance().skjul();
        expect(skjulSpy).to.have.been.called;
    });
});
