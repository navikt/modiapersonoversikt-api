import React from 'react';

class Snurrepipp extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        var src = '/modiabrukerdialog/img/ajaxloader/' + this.props.farge + '/loader_' + this.props.farge + '_' + this.props.storrelse + '.gif';
        return (
            <div className="snurrepipp">
                <img src={src} alt="Snurrepipp" />
            </div>
        );
    }
}
Snurrepipp.defaultProps = {
    storrelse: 128,
    farge: 'graa'
};

export default Snurrepipp;