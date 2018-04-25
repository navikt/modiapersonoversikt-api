/* eslint-env mocha */
/* eslint no-unused-expressions:0 */

import './../test-config';
import { assert } from 'chai';
import React from 'react';
import ReactDOM from 'react-dom';
import JournalforingsPanel from './journalforing-panel-module';
import TestUtils from 'react-dom/test-utils';

describe('Journalforing', () => {
    it('Header should be "Journalføring"', () => {
        const journalforingspanel = TestUtils.renderIntoDocument(
            <JournalforingsPanel />
        );
        const header = TestUtils.findRenderedDOMComponentWithTag(journalforingspanel, 'h2');
        assert.equal(ReactDOM.findDOMNode(header).textContent, 'Journalføring');
    });
});
