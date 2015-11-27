/* globals $,Modig */
import React from 'react/addons';
import { omit } from './../utils/utils-module';

function oppdaterScrollbarEtterRenderingErFerdig(element) {
    setTimeout(function oppdaterScroll() {
        element.mCustomScrollbar('update');
    });
}

const ScrollPortal = React.createClass({
    propTypes: {
        'innerClassName': React.PropTypes.string,
        'children': React.PropTypes.oneOfType([React.PropTypes.element, React.PropTypes.array])
    },
    componentDidMount: function componentDidMount() {
        this.renderList();
    },
    componentDidUpdate: function componentDidUpdate() {
        this.renderList();
    },
    renderList: function renderList() {
        const wrapper = $(this.refs.wrapper.getDOMNode());
        wrapper.mCustomScrollbar($.extend({}, Modig.scrollOptions, {scrollbarPosition: 'inside'}));
        const container = wrapper.find('.mCSB_container:first').get(0);
        React.render(<div className={this.props.innerClassName}>{this.props.children}</div>, container);
        oppdaterScrollbarEtterRenderingErFerdig(wrapper);
    },
    render: function render() {
        const props = omit(this.props, ['children', 'innerClassName']);
        return (
            <div ref="wrapper" {...props}></div>
        );
    }
});

export default ScrollPortal;
