import React from 'react';

class Infoboks extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="feilmelding">
                <div className="robust-ikon-feil-gra"></div>
                <span className="stor">{this.props.tekst}</span>
            </div>
        )
    }
}
export default Infoboks;