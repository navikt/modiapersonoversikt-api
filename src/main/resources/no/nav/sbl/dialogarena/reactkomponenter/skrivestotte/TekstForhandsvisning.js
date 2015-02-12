/** @jsx React.DOM */
var React = require('react');
var LocaleSelect = require('./LocaleSelect');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var innhold = this.props.valgtTekst.innhold;
        var tekst = innhold[this.props.valgtLocale] ? innhold[this.props.valgtLocale] : innhold['nb_NO'];
        tekst = tekst.split(/[\r\n]+/);

        return (
            <div className="tekstForhandsvisning">
                <LocaleSelect valgtTekst={this.props.valgtTekst} valgtLocale={this.props.valgtLocale} setValgtLocale={this.props.setValgtLocale}/>
                {tekst.map(function (avsnitt) {
                    return (
                        <p dangerouslySetInnerHTML={{__html: avsnitt}}></p>
                    );
                })}
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
