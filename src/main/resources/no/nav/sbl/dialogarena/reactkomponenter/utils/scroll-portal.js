var React = require('react/addons');
var omit = require('./../utils/utils-module').omit;

var ScrollPortal = React.createClass({
    renderList: function () {
        var wrapper = $(this.refs.wrapper.getDOMNode());
        // Dette gjør at testen feiler Modig.scrollOptions , window.Modig = {}
        wrapper.mCustomScrollbar($.extend({}, Modig.scrollOptions, {scrollbarPosition: 'inside'}));
        var container = wrapper.find('.mCSB_container:first').get(0);
        React.render(<div className={this.props.innerClassName}>{this.props.children}</div>, container);
    },
    componentDidMount: function () {
        this.renderList();
    },
    componentDidUpdate: function () {
        this.renderList();
    },
    render: function () {
        var props = omit(this.props, ['children', 'innerClassName']);
        return (
            <div ref="wrapper" {...props}></div>
        );
    }
});

module.exports = ScrollPortal;