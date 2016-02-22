/* eslint-env mocha */
import './../test-config';
import { expect } from 'chai';
import sinon from 'sinon';
import React from 'react';
import Utils from './../utils/utils-module';
import TekstForhandsvisning from './tekst-forhandsvisning';
const TestUtils = React.addons.TestUtils;

describe('TekstForhandsvisning', () => {
    const tekst = {innhold: {'nb_NO': 'tekst'}, tags: []};

    it('splitter tekst i avsnitt', () => {
        sinon.spy(Utils, 'tilParagraf');

        const shallowRenderer = TestUtils.createRenderer();
        shallowRenderer.render(
            <TekstForhandsvisning tekst={tekst} />
        );

        expect(Utils.tilParagraf.called).to.equal(true);

        Utils.tilParagraf.restore();
    });
});
