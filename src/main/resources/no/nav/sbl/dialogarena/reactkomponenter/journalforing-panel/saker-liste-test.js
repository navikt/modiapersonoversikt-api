require('./../test-config');
var expect = require('chai').expect;
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var sinon = require('sinon');
require('sinon-chai');
var SakerListe = require('./saker-liste');

describe('SakerListe', function () {

    it('skal gruppere skaer på samme temaKode', function () {
        var saker = [
            {temaKode: 'DAG'},
            {temaKode: 'DAG'},
            {temaKode: 'AAP'},
            {temaKode: 'DAG'},
            {temaKode: 'BIL'}
        ];

        var sakerListe = TestUtils.renderIntoDocument(<SakerListe saker={saker}/>);
        var renderedDOM = React.findDOMNode(sakerListe);
        var sakerTema = renderedDOM.querySelectorAll('.saker-tema');

        expect(sakerTema.length).to.equal(3);
    });

});
