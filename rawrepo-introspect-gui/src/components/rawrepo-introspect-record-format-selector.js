/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonToolbar} from "react-bootstrap";
const queryString = require('query-string');

class RawrepoIntrospectRecordFormatSelector extends React.Component {

    constructor(props) {
        super(props);

        RawrepoIntrospectRecordFormatSelector.setFormatLine = RawrepoIntrospectRecordFormatSelector.setFormatLine.bind(this);
        RawrepoIntrospectRecordFormatSelector.setFormatMarcXchange = RawrepoIntrospectRecordFormatSelector.setFormatMarcXchange.bind(this);
    }

    static setFormatLine() {
        const queryParams = queryString.parse(location.search);
        queryParams.format = 'line';
        location.search = queryString.stringify(queryParams);
    }

    static setFormatMarcXchange() {
        const queryParams = queryString.parse(location.search);
        queryParams.format = 'xml';
        location.search = queryString.stringify(queryParams);
    }

    render() {
        return (
            <div>
                <ButtonToolbar>
                    <Button onClick={RawrepoIntrospectRecordFormatSelector.setFormatLine}>Line</Button>
                    <Button onClick={RawrepoIntrospectRecordFormatSelector.setFormatMarcXchange}>MarcXchange</Button>
                </ButtonToolbar>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordFormatSelector;