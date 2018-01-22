/* eslint no-unused-expressions:0 */
import './../test-config';
import React from 'react';
import { shallow, mount } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import MeldingerSokModule from './meldinger-sok-module';
import MeldingerSokModal from './meldinger-sok-modal';

describe('Meldinger Sok Module', () => {
    it('Skal rendre en MeldingerSokView inni en MeldingerSokModal', () => {
        const stub = sinon.stub(MeldingerSokModule.prototype, 'componentDidMount');

        const element = shallow(<MeldingerSokModule />);

        stub.restore();

        expect(element.is('MeldingerSokModal')).to.be.true;
        expect(element.find('MeldingerSokView').length).to.equal(1);
    });
});
