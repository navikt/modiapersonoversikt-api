require('./../../testConfig.js');
var expect = require('chai').expect;
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var sinon = require('sinon');
var LukkKnapp= require('../LukkKnapp');

describe('LukkKnapp test', function () {

    it('Header should be "Journalføring"', function(){
        window.Wicket = {
            Ajax: {
                ajax: function(){}
            }
        };
        sinon.spy(window.Wicket.Ajax, 'ajax');
        var lukkKnapp = TestUtils.renderIntoDocument(
            <LukkKnapp/>
        );
        var knapp=React.findDOMNode(lukkKnapp);
        TestUtils.Simulate.click(knapp);
        expect(window.Wicket.Ajax.ajax.calledOnce).to.equal(true);
        delete window.Wicket.Ajax;
    });

});
