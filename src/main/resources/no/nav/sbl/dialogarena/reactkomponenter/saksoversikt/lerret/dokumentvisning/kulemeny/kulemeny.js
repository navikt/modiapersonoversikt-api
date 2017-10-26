import React from 'react';
import PT from 'prop-types';
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
    dokref: PT.string.isRequired,
    tittel: PT.string.isRequired,
    initialState: PT.bool.isRequired
};

export default Kulemeny;
