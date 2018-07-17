import '../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import SlaaSammenTraader from './slaa-sammen-traader-module';

const slaaSammenTraaderPropsMock = {
    traadIder: [],
    fnr: '',
    wicketurl: '',
    wicketcomponent: ''
};

describe('<SlaaSammenTraader> komponent', () => {
    it('Rendrer et MeldingerSok', () => {
        const node = shallow(<SlaaSammenTraader {...slaaSammenTraaderPropsMock} />);
        expect(node.is('MeldingerSok')).to.be.true;
    });
});

//TODO bedre tester