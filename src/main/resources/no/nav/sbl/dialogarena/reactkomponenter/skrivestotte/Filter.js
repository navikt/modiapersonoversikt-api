/** @jsx React.DOM */
var React = ModiaJS.React;

var Filter = React.createClass({
    sok: function (event) {
        this.props.sok(event.nativeEvent.target.value);
    },
    render: function () {
        return (
            <div className="filter-container">
                <input type="text" placeholder="Søk" onKeyUp={this.sok} />
            </div>
        );
    }
});

module.exports = Filter;
