/** @jsx React.DOM */
var React = require('react');

var TekstListe = React.createClass({
    setValgtTekst: function (tekst) {
        var that = this;
        return function () {
            that.props.setValgtTekst(tekst);
        };
    },
    render: function () {
        var that = this;
        var tekster = this.props.tekster;

        return (
            <ul className="tekstListe">
            {
                tekster.filter(function (tekst) {
                    return that.props.filter.temagruppe === tekst.temagruppe;
                }).map(function (tekst) {
                    return (
                        <li onClick={that.setValgtTekst(tekst)}>
                            <h2>{tekst.tittel}</h2>
                            <span>{tekst.ingress}</span>
                        </li>
                    );
                })
                }
            </ul>
        );
    }
});

module.exports = TekstListe;
