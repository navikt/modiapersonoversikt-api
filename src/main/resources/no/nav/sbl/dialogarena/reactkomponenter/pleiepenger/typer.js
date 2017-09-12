import React from 'react';

export const javaDatoType = React.PropTypes.shape({
    year: React.PropTypes.number.isRequired,
    monthValue: React.PropTypes.number.isRequired,
    dayOfMonth: React.PropTypes.number.isRequired
});
