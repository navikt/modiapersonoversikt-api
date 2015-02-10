/** @jsx React.DOM */
var React = require('react');
var LocaleSelect = React.createClass({
    onChange: function (event) {
        this.props.setValgtLocale(event.nativeEvent.target.value)
    },
    render: function () {
        var options = [];
        options.push(<option value="">nb</option>);
        if (this.props.hjelpetekst.locales) {
            options.push(Object.keys(this.props.hjelpetekst.locales).map(function (locale) {
                return <option value={locale}>{locale}</option>;
            }.bind(this)));
        }
        return (
            <select onChange={this.onChange} value={this.props.valgtLocale}>{options}</select>
        );
    }
});

module.exports = LocaleSelect;

