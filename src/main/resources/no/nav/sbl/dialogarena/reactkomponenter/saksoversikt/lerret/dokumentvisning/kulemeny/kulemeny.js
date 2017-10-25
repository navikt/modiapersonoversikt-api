import React, { PropTypes as pt } from 'react';
import { Button } from 'react-scroll';

const Kulemeny = ({ dokref, tittel, initialState }) => (
    <li>
        <Button
            activeClass="active"
            to={dokref}
            spy={true}
            smooth={true}
            duration={500}
            offset={-80}
            title={tittel}
            isDynamic={true}
            type="button"
            containerId="js-kulemeny-scroll"
        />
    </li>
);

Kulemeny.propTypes = {
    dokref: pt.string.isRequired,
    tittel: pt.string.isRequired,
    initialState: pt.bool.isRequired
};

export default Kulemeny;
