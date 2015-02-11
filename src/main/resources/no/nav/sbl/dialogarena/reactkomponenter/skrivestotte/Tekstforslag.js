/** @jsx React.DOM */
var React = require('react');

var TekstListe = require('./TekstListe');
var TekstForhandsvisning = require('./TekstForhandsvisning');
var LocaleSelect = require('./LocaleSelect');
var Filter = require('./Filter');

var Tekstforslag = React.createClass({
    getInitialState: function () {
        return {tekster: [], valgtTekst: {innhold : {}}, valgtLocale: 'nb_NO', fritekst: '', show: true};
    },
    componentDidMount: function () {
        hentEnonicTekster('').done(function (tekster) {
            this.setState({tekster: tekster});
        }.bind(this));
    },
    toggle: function () {
        this.setState({show: !this.state.show});
    },
    setValgtTekst: function (tekst) {
        this.setState({valgtTekst: tekst, valgtLocale: 'nb_NO'})
    },
    setValgtLocale: function (locale) {
        this.setState({valgtLocale: locale})
    },
    setFritekst: function (fritekst) {
        this.setState({fritekst: fritekst});
        hentEnonicTekster(fritekst).done(function (tekster) {
            this.setState({tekster: tekster});
        }.bind(this));
    },
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId).focus().val(this.state.valgtTekst.innhold[this.state.valgtLocale]);
        this.setState({show: false});
    },
    render: function () {
        return this.state.show ? (
            <div className="tekstforslag">
                <Filter setFritekst={this.setFritekst} tekst={this.state.fritekst} />
                <LocaleSelect valgtTekst={this.state.valgtTekst} valgtLocale={this.state.valgtLocale} setValgtLocale={this.setValgtLocale}/>
                <div className="tekstvisning">
                    <TekstListe tekster={this.state.tekster} valgtTekst={this.state.valgtTekst} setValgtTekst={this.setValgtTekst} />
                    <TekstForhandsvisning valgtTekst={this.state.valgtTekst} valgtLocale={this.state.valgtLocale} />
                </div>
                <input type="button" value="Velg tekst" onClick={this.settInnTekst}/>
            </div>
        ) : null;
    }

});

function hentEnonicTekster(fritekst) {
    return $.get('/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + fritekst);
}

module.exports = Tekstforslag;
