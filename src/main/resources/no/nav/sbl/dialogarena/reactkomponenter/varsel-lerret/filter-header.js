import React, { PropTypes as PT } from 'react';

class FilterHeader extends React.Component {

    render() {
        const { style } = this.props;
        return (
            <div className="filter-header" style={style}>

            </div>
        );
    }
}

FilterHeader.propTypes = {
    style: PT.object
};

export default FilterHeader;
