/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonToolbar} from "react-bootstrap";
const queryString = require('query-string');

class RawrepoIntrospectRecordModeSelector extends React.Component {

    constructor(props) {
        super(props);

        RawrepoIntrospectRecordModeSelector.setModeRaw = RawrepoIntrospectRecordModeSelector.setModeRaw.bind(this);
        RawrepoIntrospectRecordModeSelector.setModeMerged = RawrepoIntrospectRecordModeSelector.setModeMerged.bind(this);
        RawrepoIntrospectRecordModeSelector.setModeExpanded = RawrepoIntrospectRecordModeSelector.setModeExpanded.bind(this);
    }

    static setModeRaw() {
        const queryParams = queryString.parse(location.search);
        queryParams.mode = 'raw';
        location.search = queryString.stringify(queryParams);
    }

    static setModeMerged() {
        const queryParams = queryString.parse(location.search);
        queryParams.mode = 'merged';
        location.search = queryString.stringify(queryParams);
    }

    static setModeExpanded() {
        const queryParams = queryString.parse(location.search);
        queryParams.mode = 'expanded';
        location.search = queryString.stringify(queryParams);
    }

    render() {
        return (
            <div>
                <ButtonToolbar>
                    <Button onClick={RawrepoIntrospectRecordModeSelector.setModeRaw}>Raw</Button>
                    <Button onClick={RawrepoIntrospectRecordModeSelector.setModeMerged}>Merged</Button>
                    <Button onClick={RawrepoIntrospectRecordModeSelector.setModeExpanded}>Expanded</Button>
                </ButtonToolbar>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordModeSelector;