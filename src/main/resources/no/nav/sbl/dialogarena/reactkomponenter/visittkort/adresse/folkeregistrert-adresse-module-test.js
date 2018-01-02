import '../../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import FolkeregistrertAdresse from './folkeregistrert-adresse-module';

describe('Folkeregistrert-adrese', () => {
    it('skal rendre', () => {
        const element = shallow(<FolkeregistrertAdresse />);

        expect(element.find('.overskrift').length).to.equal(1);
    });
});
