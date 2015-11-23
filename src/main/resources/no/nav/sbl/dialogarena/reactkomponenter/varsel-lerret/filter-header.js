import React from 'react';

class FilterHeader extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        const {style} = this.props;
        return (
            <div className="filter-header" style={style}>

            </div>
        );
    }
}

FilterHeader.propTypes = {
    'style': React.PropTypes.string
};

export default FilterHeader;
