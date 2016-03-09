import React from 'react';
import { Button } from 'react-scroll';

const Kulemeny = ({ dokref, tittel }) => (
    <li>
        <Button activeClass="active" to={dokref} spy smooth duration={500} offset={-80} title={tittel} type="button" containerId="js-kulemeny-scroll" />
    </li>
);

Kulemeny.propTypes = {
    dokref: React.PropTypes.string.isRequired,
    tittel: React.PropTypes.string.isRequired
};

export default Kulemeny;
