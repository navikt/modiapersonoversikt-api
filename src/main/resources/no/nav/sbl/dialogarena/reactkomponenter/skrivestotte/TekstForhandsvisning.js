/** @jsx React.DOM */
var React = require('react');

var Utils = require('./Utils');

var LocaleSelect = require('./LocaleSelect');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var element = this.props.element.hasOwnProperty('innhold') ?
            this.props.element : {innhold: {nb_NO: ''}, tags: []};

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
                {element.tags.map(function (tag) {
                    return (
                        <span className="knagg">{'#' + tag}</span>
                    );
                })}
                </div>
                <div className="velgPanel">
                    <LocaleSelect valgtTekst={element} valgtLocale={this.props.locale} setValgtLocale={this.props.settLocale}/>
                    <input type="submit" value="Velg tekst" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
