import React, { Component } from 'react';
import Ekspanderbartpanel from 'nav-frontend-ekspanderbartpanel';
import PT from 'prop-types';

class Kategoripanel extends Component {

    tittel() {
        return (
            <h1 className="medium dialogpanel-header">
                {this.props.tittel}
            </h1>
        );
    }

    render() {
        return (
            <Ekspanderbartpanel
                className="kategoripanel kanaloverskrift"
                tittel={this.tittel()}
                apen={this.props.apen}
            >
                {this.props.children}
            </Ekspanderbartpanel>
        );
    }
}

Kategoripanel.propTypes = {
    apen: PT.bool,
    tittel: PT.string.isRequired,
    children: PT.node.isRequired
};
Kategoripanel.defaultProps = {
    apen: false
};

export default Kategoripanel;
