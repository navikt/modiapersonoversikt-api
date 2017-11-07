import React from 'react';
import Ekspanderbartpanel from 'nav-frontend-ekspanderbartpanel';
import PT from 'prop-types';

function Kategoripanel(props) {
    function lagTittel() {
        return (
            <h1 className="medium dialogpanel-header">
                {props.tittel}
            </h1>
        );
    }

    return (
        <Ekspanderbartpanel
            className="kategoripanel kanaloverskrift"
            tittel={lagTittel()}
            apen={props.apen}
        >
            {props.children}
        </Ekspanderbartpanel>
    );
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
