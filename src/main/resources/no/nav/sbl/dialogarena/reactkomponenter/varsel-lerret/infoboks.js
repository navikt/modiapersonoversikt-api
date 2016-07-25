import React, { PropTypes as PT } from 'react';

class Infoboks extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { tekst, style } = this.props;
        return (
            <div className="feilmelding" style={style}>
                <div className="robust-ikon-feil-gra"></div>
                <span className="stor">{tekst}</span>
            </div>
        );
    }
}

Infoboks.propTypes = {
    tekst: PT.string.isRequired,
    style: PT.object
};

export default Infoboks;
