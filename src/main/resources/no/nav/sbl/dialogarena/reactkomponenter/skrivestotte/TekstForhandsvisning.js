/** @jsx React.DOM */
var React = require('react/addons');
var Utils = require('./../utils');
var ScrollPortal = require('./../utils/ScrollPortal.js');
var LocaleSelect = require('./LocaleSelect');


var TekstForhandsvisning = React.createClass({
    render: function () {
        var tekst = this.props.tekst.hasOwnProperty('innhold') ? this.props.tekst : {innhold: {nb_NO: ''}, tags: []};

        var paragrafer = Utils.getInnhold(tekst, this.props.locale)
            .split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        paragrafer = React.addons.createFragment({
            paragrafer: paragrafer
        });

        var knagger = tekst.tags.map(function (tag) {
            return (
                <button key={tag} className="knagg" onClick={onClickProxy.bind(this.props.store, tag)}>
                    <span>{'#' + tag}</span>
                </button>
            );
        }.bind(this));

        return (
            <div>
                <ScrollPortal className="tekstPanel" innerClassName="tekst-panel-wrapper">
                {paragrafer}
                {knagger}
                </ScrollPortal>
                <div className="velgPanel">
                    <LocaleSelect tekst={tekst} locale={this.props.locale} store={this.props.store}/>
                    <input type="submit" value="Velg tekst" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
});

function onClickProxy(tag, event) {
    event.preventDefault();
    this.leggTilKnagg(tag);
}

module.exports = TekstForhandsvisning;
