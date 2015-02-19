/** @jsx React.DOM */
require('./../../testConfig.js');
var expect = require('chai').expect;
var React = window.ModiaJS.React = require('react/addons');

describe('Test', function(){
    it('test 1', function() {
        var KnaggInput = require('../index.js');
        var TestUtils = React.addons.TestUtils;

        var props = {
            'fritekst': '',
            'knagger': [],
            'placeholder': 'Søk',
            'onChange': function(){},
            'onKeyDown': function(){},
            'aria-label': 'Søk etter hjelpetekster',
            'aria-controls': 'tekstListePanel',
            'auto-focus': false
        };


        var knaggElement = TestUtils.renderIntoDocument(
            <KnaggInput {...props} />
        );

        var knaggcontainer = TestUtils.findRenderedDOMComponentWithClass(knaggElement, 'knagg-input');

        expect(knaggcontainer).not.to.be.null;
    })
});