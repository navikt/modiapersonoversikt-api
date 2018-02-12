import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import MeldingerSokModule from './meldinger-sok-module';

describe('Meldinger Sok Module', () => {
    it('Skal rendre en MeldingerSokView inni en MeldingerSokModal', () => {
        const componentDidMountStub = sinon.stub(MeldingerSokModule.prototype, 'componentDidMount');

        const element = shallow(<MeldingerSokModule />);

        componentDidMountStub.restore();

        expect(element.is('MeldingerSokModal')).to.be.true;
        expect(element.find('MeldingerSokView').length).to.equal(1);
    });
});
