import React from 'react';
import { pick, contains } from 'lodash';

class JournalforKnapp extends React.Component {
    constructor(props) {
        super(props);
        this.journalfor = this.journalfor.bind(this);
        this.state = {
            sender: false,
            sendt: false,
            feilet: false
        }
    }

    journalfor() {
        let sak = pick(this.props.sak, (property) => {
            return contains([
                'saksId',
                'temakode',
                'finnesIGsak',
                'finnesIPsak',
            ], property);
        });

        $.ajax({
            type: 'POST',
            url: '/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/' + this.props.traadId,
            contentType: 'application/json',
            data: JSON.stringify(sak)
        })
            .done(function (response, status, xhr) {
                this.setState({sender: false, sendt: true});
            }.bind(this))
            .fail(function () {
                this.setState({feilet: true, sender: false})
            }.bind(this));
    }

    render() {
        return (
            <button className="journalfor-knapp knapp-advarsel-stor" onClick={this.journalfor}>Journalfør</button>
        );
    }
}

export default JournalforKnapp;