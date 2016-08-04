/* globals $ */
import React from 'react';
import { omit } from './../utils/utils-module';

const ScrollPortal = React.createClass({
    propTypes: {
        'innerClassName': React.PropTypes.string,
        'children': React.PropTypes.oneOfType([React.PropTypes.element, React.PropTypes.array])
    },
    render: function render() {
        const props = omit(this.props, ['children', 'innerClassName']);
        return (
            <div ref="wrapper" {...props}>{this.props.children}</div>
        );
    }
});

export default ScrollPortal;
