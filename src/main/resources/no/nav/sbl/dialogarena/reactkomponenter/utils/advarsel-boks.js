import React from 'react';

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
    tekst: React.PropTypes.string.isRequired
};

export default AdvarselBoks;
