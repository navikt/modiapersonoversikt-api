require('./../test-config');
var expect = require('chai').expect;
var React = require('react/addons');
var assign = require('object-assign');
var KnaggInput = require('./knagginput-module');

describe('KnaggInput', function () {
    var defaultProps = {
        'fritekst': '',
        'knagger': [],
        'placeholder': 'Søk',
        'onChange': function () {
        },
        'onKeyDown': function () {
        },
        'aria-label': 'Søk etter skrivestøttetekster',
        'aria-controls': 'tekstListePanel',
        'auto-focus': false
    };

    it('Lager komponenten', function () {
        var TestUtils = React.addons.TestUtils;

        var props = assign({}, defaultProps);

        var knaggElement = TestUtils.renderIntoDocument(
            <KnaggInput {...props} />
        );

        var knaggcontainer = TestUtils.findRenderedDOMComponentWithClass(knaggElement, 'knagg-input');
        var knagg = TestUtils.scryRenderedDOMComponentsWithClass(knaggElement, 'knagg');

        expect(knaggcontainer).not.to.be.null;
        expect(knagg.length).to.be.eql(0);
    });

    it('Legger inn default knagger', function () {
        var TestUtils = React.addons.TestUtils;

        var props = assign({}, defaultProps);
        props.knagger.push('testknagg');

        var knaggElement = TestUtils.renderIntoDocument(
            <KnaggInput {...props} />
        );

        var knagg = TestUtils.scryRenderedDOMComponentsWithClass(knaggElement, 'knagg');

        expect(knagg.length).to.be.eql(1);
    });
});