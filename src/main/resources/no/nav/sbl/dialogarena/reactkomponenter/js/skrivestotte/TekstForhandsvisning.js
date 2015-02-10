/** @jsx React.DOM */
var React = require('react');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var tekst = this.props.valgtLocale ? this.props.tekst.locales[this.props.valgtLocale] : this.props.tekst.innhold;
        tekst = tekst ? tekst.split(/[\r\n]+/) : [];

        return (
            <div className="tekstForhandsvisning">
            {tekst.map(function (t) {
                return (<p>{t}</p>);
            })}
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
