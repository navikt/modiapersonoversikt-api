/** @jsx React.DOM */
var React = require('react');

var TekstListe = require('./TekstListe');
var TekstForhandsvisning = require('./TekstForhandsvisning');

var Tekstvisning = React.createClass({
    render: function () {
        return this.props.tekster.length > 0 ?
            (<div className="tekstvisning">
                <TekstListe tekster={this.props.tekster} valgtTekst={this.props.valgtTekst} setValgtTekst={this.props.setValgtTekst} />
                <TekstForhandsvisning valgtTekst={this.props.valgtTekst} valgtLocale={this.props.valgtLocale} setValgtLocale={this.props.setValgtLocale} />
            </div>) :
            (<div className="tekstforslag">
                <h1 className="tomt">Fant ingen tekster</h1>
            </div>);
    }
});

module.exports = Tekstvisning;