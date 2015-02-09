/** @jsx React.DOM */
var React = require('react');

var TekstListe = require('./TekstListe');
var TekstForhandsvisning = require('./TekstForhandsvisning');
var Filter = require('./Filter');
var MockData = require('./MockData');

var Tekstforslag = React.createClass({
    getInitialState: function () {
        return {tekster: [], valgtTekst: {}, filterVerdier: {}, show: true};
    },
    componentDidMount: function () {
        this.setState({tekster: MockData});
    },
    setValgtTekst: function (tekst) {
        this.setState({valgtTekst: tekst})
    },
    setFilter: function (filter) {
        this.setState({filterVerdier: filter});
    },
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId).focus().val(this.state.valgtTekst.tekst);
        this.setState({show: false});
    },
    render: function () {
        return this.state.show ? (
            <div className="tekstforslag">
                <Filter setFilter={this.setFilter} />
                <div>
                    <TekstListe tekster={this.state.tekster} filter={this.state.filterVerdier} setValgtTekst={this.setValgtTekst} />
                    <TekstForhandsvisning tekst={this.state.valgtTekst} />
                </div>
                <input type="button" value="Velg tekst" onClick={this.settInnTekst}/>
            </div>
        ) : null;
    }

});

module.exports = Tekstforslag;
