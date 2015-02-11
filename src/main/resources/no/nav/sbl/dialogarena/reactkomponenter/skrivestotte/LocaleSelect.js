/** @jsx React.DOM */
var React = require('react');
var LocaleSelect = React.createClass({
    onChange: function (event) {
        this.props.setValgtLocale(event.nativeEvent.target.value)
    },
    render: function () {
        return (
            <select onChange={this.onChange} value={this.props.valgtLocale}>
            {Object.keys(this.props.hjelpetekst.locales).map(function (locale) {
                return <option value={locale}>{locale}</option>;
            }.bind(this))}
            </select>
        );
    }
});

module.exports = LocaleSelect;

