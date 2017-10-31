/* globals $ */
import React from 'react';
import PT from 'prop-types';

import { omit } from './../utils/utils-module';

/* eslint "react/prefer-es6-class": 1 */
const ScrollPortal = React.createClass({
    propTypes: {
        innerClassName: PT.string,
        children: PT.oneOfType([PT.element, PT.array])
    },
    render: function render() {
        const props = omit(this.props, ['children', 'innerClassName']);
        return (
            <div ref="wrapper" {...props}>{this.props.children}</div>
        );
    }
});

export default ScrollPortal;
