import React, { Component } from 'react';
import PT from 'prop-types';

import { omit } from './../utils/utils-module';

class ScrollPortal extends Component {
    render() {
        const fiteredProps = omit(this.props, ['children', 'innerClassName']);
        return (
            <div ref="wrapper" {...fiteredProps}>{this.props.children}</div>
        );
    }
}

ScrollPortal.propTypes = {
    innerClassName: PT.string,
    children: PT.oneOfType([PT.element, PT.array])
};

export default ScrollPortal;
