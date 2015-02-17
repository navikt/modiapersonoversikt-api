/** @jsx React.DOM */
var React = ModiaJS.React;

var Utils = require('./Utils');

var LocaleSelect = require('./LocaleSelect');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var tekst = Utils.getInnhold(this.props.valgtTekst, this.props.valgtLocale);
        tekst = tekst.split(/[\r\n]+/);

        return (
            <div className="tekstForhandsvisning" role="tabpanel" id="tekstForhandsvisningPanel" aria-atomic="true" aria-live="polite">
                <div className="tekstPanel">
                {tekst.map(function (avsnitt) {
                    return (
                        <p dangerouslySetInnerHTML={{__html: avsnitt}}></p>
                    );
                })}
                </div>
                <div className="velgPanel">
                    <LocaleSelect valgtTekst={this.props.valgtTekst} valgtLocale={this.props.valgtLocale} setValgtLocale={this.props.setValgtLocale}/>
                    <input type="button" value="Velg tekst" className="knapp-hoved-liten" onClick={this.props.settInnTekst}/>
                </div>
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
