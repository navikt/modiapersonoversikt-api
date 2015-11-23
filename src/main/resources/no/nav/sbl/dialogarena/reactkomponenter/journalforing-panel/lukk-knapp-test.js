/* eslint-env mocha */
/* eslint no-unused-expressions:0 */
import './../test-config';
import { expect } from 'chai';
import React from 'react/addons';
import sinon from 'sinon';
import 'sinon-chai';
import LukkKnapp from './lukk-knapp';
const TestUtils = React.addons.TestUtils;

describe('LukkKnapp test', () => {
    it('Header should be "Journalføring"', () =>{
        window.Wicket = {
            Ajax: {
                ajax: () => ({})
            }
        };
        sinon.spy(window.Wicket.Ajax, 'ajax');
        const lukkKnapp = TestUtils.renderIntoDocument(
            <LukkKnapp/>
        );
        const knapp = React.findDOMNode(lukkKnapp);
        TestUtils.Simulate.click(knapp);
        expect(window.Wicket.Ajax.ajax).to.have.been.calledOnce;
        delete window.Wicket.Ajax;
    });
});
