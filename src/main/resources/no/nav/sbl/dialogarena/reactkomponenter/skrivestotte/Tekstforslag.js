/** @jsx React.DOM */
var React = require('react');

var TekstListe = require('./TekstListe');
var TekstForhandsvisning = require('./TekstForhandsvisning');
var Filter = require('./Filter');

var Tekstforslag = React.createClass({
    getInitialState: function () {
        return {tekster: [], valgtTekst: {innhold: {nb_NO: ''}}, valgtLocale: 'nb_NO', show: false};
    },
    componentDidMount: function () {
        hentEnonicTekster('').done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {}},
                tekster: tekster
            });
        }.bind(this));
    },
    toggle: function () {
        this.setState({show: !this.state.show});
    },
    setValgtTekst: function (tekst) {
        this.setState({valgtTekst: tekst})
    },
    setValgtLocale: function (locale) {
        this.setState({valgtLocale: locale})
    },
    setSokTekst: function (sokTekst) {
        hentEnonicTekster(sokTekst).done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {}},
                tekster: tekster
            });
        }.bind(this));
    },
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId).focus().val(this.state.valgtTekst.innhold[this.state.valgtLocale]);
        this.setState({show: false});
    },
    render: function () {
        if (!this.state.show) {
            return null;
        }

        if (this.state.tekster.length > 0) {
            return (
                <div className="tekstforslag">
                    <Filter setSokTekst={this.setSokTekst} />
                    <div className="tekstvisning">
                        <TekstListe tekster={this.state.tekster} valgtTekst={this.state.valgtTekst} setValgtTekst={this.setValgtTekst} />
                        <TekstForhandsvisning valgtTekst={this.state.valgtTekst} valgtLocale={this.state.valgtLocale} setValgtLocale={this.setValgtLocale} />
                    </div>
                    <input type="button" value="Velg tekst" onClick={this.settInnTekst}/>
                </div>
            );
        }

        return (
            <div className="tekstforslag">
                <h1 className="tomt">Fant ingen tekster</h1>
            </div>);
    }

});

function hentEnonicTekster(fritekst) {
    return $.get('/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + fritekst);
}

module.exports = Tekstforslag;
