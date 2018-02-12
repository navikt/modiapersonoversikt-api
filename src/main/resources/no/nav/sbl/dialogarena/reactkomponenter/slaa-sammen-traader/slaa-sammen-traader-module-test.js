import '../test-config';
import React from 'react';
import { shallow, mount, render } from 'enzyme';
import { expect } from 'chai';

import SlaaSammenTraader from './slaa-sammen-traader-module';

describe('<SlaaSammenTraader> komponent', () => {
    it('Rendrer et MeldingerSok', () => {
        const node = shallow(<SlaaSammenTraader />);
        expect(node.is('MeldingerSok')).to.be.true;
    });
});

//TODO bedre tester