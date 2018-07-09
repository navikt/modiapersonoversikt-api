import React from 'react';
import PT from 'prop-types';


class AdvarselBoks extends React.Component {
    render() {
        return (
            <div className="advarsel-boks warn">
                <p>{this.props.tekst}</p>
            </div>
        );
    }
}

AdvarselBoks.propTypes = {
    tekst: PT.string.isRequired
};

export default AdvarselBoks;
