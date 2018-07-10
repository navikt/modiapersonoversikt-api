import React from 'react';
import PT from 'prop-types';

function FilterHeader({ style }) {
    return (
        <div className="filter-header" style={style}>

        </div>
    );
}

FilterHeader.propTypes = {
    style: PT.object
};

export default FilterHeader;
