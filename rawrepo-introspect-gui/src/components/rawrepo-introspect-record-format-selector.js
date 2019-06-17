/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

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
        const queryParams = queryString.parse(location.search);
        const format = queryParams.format;

        return (
            <div>
                <div id='format-div'>
                    <ButtonGroup id='button-tool-bar-format'>
                        <Button onClick={RawrepoIntrospectRecordFormatSelector.setFormatLine}
                                bsStyle={format === 'line' ? 'primary' : 'default'}
                                id='button-format-line'>Line</Button>
                        <Button onClick={RawrepoIntrospectRecordFormatSelector.setFormatMarcXchange}
                                bsStyle={format === 'xml' ? 'primary' : 'default'}
                                id='button-format-xml'>MarcXchange</Button>
                    </ButtonGroup>
                </div>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordFormatSelector;