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
            <li onClick={this.setValgtTekst(tekst)}>
                <h2>{tekst.tittel}</h2>
                <span>{tekst.innhold}</span>
            </li>
        );
    }
});

module.exports = TekstListe;
