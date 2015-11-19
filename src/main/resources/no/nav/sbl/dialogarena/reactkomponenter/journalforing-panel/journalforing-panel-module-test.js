import './../test-config';
import { expect, assert } from 'chai';
import React from 'react/addons';
import JournalforingsPanel from './journalforing-panel-module';
const TestUtils = React.addons.TestUtils;

describe('Journalforing', function () {

    it('Header should be "Journalføring"', function () {
        const journalforingspanel = TestUtils.renderIntoDocument(
            <JournalforingsPanel/>
        );
        const header = TestUtils.findRenderedDOMComponentWithTag(journalforingspanel, 'h2');
        assert.equal(header.getDOMNode().textContent, 'Journalføring');
    });

});