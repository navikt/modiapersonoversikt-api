require('./../../testConfig.js');
var expect = require('chai').expect;
var assert = require('chai').assert;
var React = require('react/addons');
var JournalforingsPanel = require('../index.js');
var TestUtils = React.addons.TestUtils;


describe.only('Journalforing', function () {

    it('null is null test', function(){
        expect(null).to.be.null;
    });

    it('Header should be "Journalføring"', function(){
       var journalforingspanel = TestUtils.renderIntoDocument(
           <JournalforingsPanel/>
       );
        var header = TestUtils.findRenderedDOMComponentWithTag(journalforingspanel, 'h2');
        assert.equal(header.getDOMNode().textContent, 'Journalføring');
    });

});