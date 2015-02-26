/** @jsx React.DOM */
var React = require('react');

var Utils = require('./Utils');

var LocaleSelect = require('./LocaleSelect');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var element = this.props.element.hasOwnProperty('innhold') ? this.props.element : {innhold: {nb_NO: ''}};

        var tekst = Utils.getInnhold(element, this.props.locale);
        tekst = tekst.split(/[\r\n]+/);

        return (
            <div>
                <div className="tekstPanel">
                {tekst.map(function (avsnitt) {
                    return (
                        <p dangerouslySetInnerHTML={{__html: avsnitt}}></p>
                    );
                })}
                </div>
                <div className="velgPanel">
                    <LocaleSelect valgtTekst={element} valgtLocale={this.props.locale} setValgtLocale={this.props.settLocale}/>
                    <input type="button" value="Velg tekst" className="knapp-hoved-liten" onClick={this.props.submit}/>
                </div>
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
