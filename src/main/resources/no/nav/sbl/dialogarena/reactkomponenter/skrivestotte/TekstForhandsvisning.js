/** @jsx React.DOM */
var React = require('react');

var Utils = require('./Utils');

var LocaleSelect = require('./LocaleSelect');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var tekst = this.props.tekst.hasOwnProperty('innhold') ? this.props.tekst : {innhold: {nb_NO: ''}, tags: []};

        var innhold = Utils.getInnhold(tekst, this.props.locale);
        innhold = innhold.split(/[\r\n]+/);

        var paragrafer = innhold.map(function (avsnitt) {
            return (
                <p dangerouslySetInnerHTML={{__html: avsnitt}}></p>
            );
        });
        var knagger = tekst.tags.map(function (tag) {
            return (
                <button className="knagg" onClick={onClickProxy.bind(this.props.store, tag)}>{'#' + tag}</button>
            );
        }.bind(this));

        return (
            <div>
                <div className="tekstPanel">
                {paragrafer}
                {knagger}
                </div>
                <div className="velgPanel">
                    <LocaleSelect tekst={tekst} locale={this.props.locale} store={this.props.store}/>
                    <input type="submit" value="Velg tekst" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
});

function onClickProxy(tag, event){
    event.preventDefault();
    this.leggTilKnagg(tag);
}

module.exports = TekstForhandsvisning;
