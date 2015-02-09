/** @jsx React.DOM */
var React = require('react');

var TekstListe = require('./TekstListe');
var TekstForhandsvisning = require('./TekstForhandsvisning');
var Filter = require('./Filter');

var Tekstforslag = React.createClass({
    getInitialState: function () {
        return {tekster: [], valgtTekst: {}, fritekst: '', show: true};
    },
    componentDidMount: function () {
        hentEnonicTekster('').done(function (tekster) {
            this.setState({tekster: tekster});
        }.bind(this));
    },
    setValgtTekst: function (tekst) {
        console.log('Setter valgt tekst: ', tekst);
        this.setState({valgtTekst: tekst})
    },
    setFritekst: function (fritekst) {
        this.setState({fritekst: fritekst});
        hentEnonicTekster(fritekst).done(function (tekster) {
            this.setState({tekster: tekster});
        }.bind(this));
    },
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId).focus().val(this.state.valgtTekst.innhold);
        this.setState({show: false});
    },
    render: function () {
        return this.state.show ? (
            <div className="tekstforslag">
                <Filter setFritekst={this.setFritekst} />
                <div>
                    <TekstListe tekster={this.state.tekster} setValgtTekst={this.setValgtTekst} />
                    <TekstForhandsvisning tekst={this.state.valgtTekst} />
                </div>
                <input type="button" value="Velg tekst" onClick={this.settInnTekst}/>
            </div>
        ) : null;
    }

});

function hentEnonicTekster(fritekst) {
    return $.get('/modiabrukerdialog/rest/skrivestotte?fritekst=' + fritekst);
}

module.exports = Tekstforslag;
