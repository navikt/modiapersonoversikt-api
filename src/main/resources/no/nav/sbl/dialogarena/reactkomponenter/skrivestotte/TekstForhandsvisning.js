/** @jsx React.DOM */
var React = require('react');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var tekst = this.props.tekst.locales[this.props.valgtLocale];
        tekst = tekst ? tekst.split(/[\r\n]+/) : [];

        return (
            <div className="tekstForhandsvisning">
            {tekst.map(function (avsnitt) {
                return (<p>{avsnitt}</p>);
            })}
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
