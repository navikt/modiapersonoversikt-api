var React = require('react/addons');
var omit = require('./../utils/utils-module').omit;

var ScrollPortal = React.createClass({
    renderList: function () {
        //var wrapper = $(this.refs.wrapper.getDOMNode());
        //wrapper.mCustomScrollbar($.extend({}, Modig.scrollOptions, {scrollbarPosition: 'inside'}));
        //var container = wrapper.find('.mCSB_container:first').get(0);
        //React.render(<div className={this.props.innerClassName}>{this.props.children}</div>, container);
        //oppdaterScrollbarEtterRenderingErFerdig(wrapper);
    },
    componentDidMount: function () {
        //this.renderList();
    },
    componentDidUpdate: function () {
        //this.renderList();
    },
    render: function () {
        var props = omit(this.props, ['children', 'innerClassName']);
        return (
            <div ref="wrapper" {...props}>{this.props.children}</div>
        );
    }
});

function oppdaterScrollbarEtterRenderingErFerdig(element) {
    setTimeout(function () {
        element.mCustomScrollbar('update');
    });
}

module.exports = ScrollPortal;