/** @jsx React.DOM */
var React = require('react');

var Utils = require('./Utils');

var LocaleSelect = require('./LocaleSelect');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var tekst = Utils.getInnhold(this.props.valgtTekst, this.props.valgtLocale);
        tekst = tekst.split(/[\r\n]+/);

        return (
            <div className="tekstForhandsvisning">
                <LocaleSelect valgtTekst={this.props.valgtTekst} valgtLocale={this.props.valgtLocale} setValgtLocale={this.props.setValgtLocale}/>
                {tekst.map(function (avsnitt) {
                    return (
                        <p dangerouslySetInnerHTML={{__html: avsnitt}}></p>
                    );
                })}
                <input type="button" value="Velg tekst" onClick={this.props.settInnTekst}/>
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
