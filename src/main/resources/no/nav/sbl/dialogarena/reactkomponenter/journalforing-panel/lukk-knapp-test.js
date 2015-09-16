require('./../test-config');
var expect = require('chai').expect;
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var sinon = require('sinon');
require('sinon-chai');
var LukkKnapp= require('./lukk-knapp');

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
        expect(window.Wicket.Ajax.ajax).to.have.been.calledOnce;
        delete window.Wicket.Ajax;
    });

});
