require('./../test-config');
var expect = require('chai').expect;
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var sinon = require('sinon');
require('sinon-chai');
var AdvarselBoks = require('./advarsel-boks');

describe('AdvarselBoks test', function(){
    it("SKal vise tekst og ha klassen warn", function(){
        var text = "TEXT"
        var advarselboks = TestUtils.renderIntoDocument(<AdvarselBoks tekst={text}/>);

        var rendered = React.findDOMNode(advarselboks);


        expect(rendered.tagName).to.equal('DIV');
        expect(rendered.classList[0]).to.equal('advarsel-boks');
        expect(rendered.classList[1]).to.equal('warn');

        var children = rendered.querySelectorAll('p');
        expect(children.length).to.equal(1);

        var avsnitt = children[0];
        expect(avsnitt.tagName).to.equal('P');
        expect(avsnitt.innerHTML).to.equal(text);
    });
});