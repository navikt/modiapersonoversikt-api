var React = require('react/addons');

var Sokliste = React.createClass({
    renderList: function () {
        var wrapper = $(this.refs.wrapper.getDOMNode());
        wrapper.mCustomScrollbar($.extend({},Modig.scrollOptions, {scrollbarPosition: 'inside'}));
        var container = wrapper.find('.mCSB_container:first').get(0);
        React.render(<div>{this.props.children}</div>, container);
    },
    componentDidMount: function () {
        this.renderList();
    },
    componentDidUpdate: function () {
        this.renderList();
    },
    render: function () {
        return (
            <div tabIndex="-1" ref="wrapper" className="sok-liste test" role="tablist" id={this.props.listePanelId}
                 aria-live="assertive" aria-atomic="true" aria-controls={this.props.forhandsvisningsPanelId}>
            </div>
        );
    }
});

module.exports = Sokliste;