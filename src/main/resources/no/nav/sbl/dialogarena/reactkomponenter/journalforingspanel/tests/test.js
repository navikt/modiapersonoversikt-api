require('./../../testConfig.js');
var expect = require('chai').expect;
var assert = require('chai').assert;
var React = require('react/addons');
var JournalforingsPanel = require('../index.js');
var TestUtils = React.addons.TestUtils;


describe('Journalforing', function () {

    it('Header should be "Journalføring"', function(){
       var journalforingspanel = TestUtils.renderIntoDocument(
           <JournalforingsPanel/>
       );
        var header = TestUtils.findRenderedDOMComponentWithTag(journalforingspanel, 'h2');
        assert.equal(header.getDOMNode().textContent, 'Journalføring');
    });

});