import React, { PropTypes as PT } from 'react';

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
