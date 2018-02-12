import React from 'react';
import PT from 'prop-types';

import { omit } from './../utils/utils-module';

function ScrollPortal(props) {
    const filteredProps = omit(props, ['children', 'innerClassName']);
    return (
        <div {...filteredProps}>
            {props.children}
        </div>
    );
}

ScrollPortal.propTypes = {
    innerClassName: PT.string,
    children: PT.oneOfType([PT.element, PT.array])
};

export default ScrollPortal;
