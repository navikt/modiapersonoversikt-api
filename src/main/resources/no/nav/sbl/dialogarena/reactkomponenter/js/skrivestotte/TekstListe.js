/** @jsx React.DOM */
var React = require('react');

var TekstListe = React.createClass({
    setValgtTekst: function (tekst) {
        var that = this;
        return function () {
            that.props.setValgtTekst(tekst);
            console.log('Oppdatering av valgte tekster: ', tekst);
        };
    },
    render: function () {
        var tekster = this.props.tekster;
        var listeElementer = tekster.map(this.lagListeElement);

        return (
            <ul className="tekstListe">
                {listeElementer}
            </ul>
        );
    },
    lagListeElement: function (tekst) {
        return (
            <li className={this.props.valgtTekst === tekst ? 'valgt' : ''} onClick={this.setValgtTekst(tekst)}>
                <h4>{tekst.tittel}</h4>
                <span>{tekst.innhold}</span>
            </li>
        );
    }
});

module.exports = TekstListe;
