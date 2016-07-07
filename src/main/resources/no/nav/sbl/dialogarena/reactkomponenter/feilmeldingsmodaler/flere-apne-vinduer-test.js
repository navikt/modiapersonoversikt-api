import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import FlereApneVinduer from './flere-apne-vinduer';

describe('FlereApneVinduer', () => {
    it('skal vise riktige tekster i en modal', () => {
        const element = shallow(<FlereApneVinduer
          hovedtekst="test-hoved"
          avbryttekst="test-avbryt"
          fortsetttekst="test-fortsett"
        />);

        expect(element.is('Modal')).to.be.true;
        expect(element.find('section').text()).to.contain('test-hoved');
        expect(element.find('section').text()).to.contain('test-avbryt');
        expect(element.find('section').text()).to.contain('test-fortsett');
    });

    it('å trykke på avbryt kaller avbryt-callback', () => {
        const avbrytSpy = sinon.spy();
        const element = shallow(<FlereApneVinduer avbrytCallback={avbrytSpy}/>);

        element.find('button').simulate('click');

        expect(avbrytSpy).to.have.been.called;
    });

    it('å trykke på fortsett kaller fortsett-callback', () => {
        const fortsettSpy = sinon.spy();
        const element = shallow(<FlereApneVinduer fortsettCallback={fortsettSpy}/>);

        element.find('a').simulate('click');

        expect(fortsettSpy).to.have.been.called;
    });

});
