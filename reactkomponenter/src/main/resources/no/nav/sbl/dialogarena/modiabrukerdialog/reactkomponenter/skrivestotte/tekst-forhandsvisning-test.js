/* eslint-env mocha */
import './../test-config';
import { expect } from 'chai';
import sinon from 'sinon';
import React from 'react';
import Utils from './../utils/utils-module';
import TekstForhandsvisning from './tekst-forhandsvisning';
import { shallow } from 'enzyme';

describe('TekstForhandsvisning', () => {
    const tekst = { innhold: { nb_NO: 'tekst' }, tags: [] };

    it('splitter tekst i avsnitt', () => {
        const spy = sinon.spy(Utils, 'tilParagraf');

        shallow(<TekstForhandsvisning tekst={tekst} locale={''} store={{}} />);

        expect(spy.called).to.equal(true);
    });
});
