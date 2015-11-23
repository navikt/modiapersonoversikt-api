/* eslint-env mocha */
/* eslint no-unused-expressions:0 */

import './../test-config';
import { assert } from 'chai';
import React from 'react/addons';
import JournalforingsPanel from './journalforing-panel-module';
const TestUtils = React.addons.TestUtils;

describe('Journalforing', () => {
    it('Header should be "Journalføring"', () => {
        const journalforingspanel = TestUtils.renderIntoDocument(
            <JournalforingsPanel/>
        );
        const header = TestUtils.findRenderedDOMComponentWithTag(journalforingspanel, 'h2');
        assert.equal(header.getDOMNode().textContent, 'Journalføring');
    });
});
