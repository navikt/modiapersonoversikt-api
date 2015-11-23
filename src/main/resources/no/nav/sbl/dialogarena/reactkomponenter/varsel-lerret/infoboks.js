import React from 'react';

class Infoboks extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const {tekst, style} = this.props;
        return (
            <div className="feilmelding" style={style}>
                <div className="robust-ikon-feil-gra"></div>
                <span className="stor">{tekst}</span>
            </div>
        );
    }
}

Infoboks.propTypes = {
    'tekst': React.PropTypes.string.isRequired,
    'style': React.PropTypes.string
};

export default Infoboks;
