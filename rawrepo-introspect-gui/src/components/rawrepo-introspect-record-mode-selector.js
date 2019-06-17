/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

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
        const queryParams = queryString.parse(location.search);
        const mode = queryParams.mode;

        return (
            <div>
                <ButtonGroup>
                    <Button onClick={RawrepoIntrospectRecordModeSelector.setModeRaw}
                            bsStyle={mode === 'raw' ? 'primary' : 'default'}>Raw</Button>
                    <Button onClick={RawrepoIntrospectRecordModeSelector.setModeMerged}
                            bsStyle={mode === 'merged' ? 'primary' : 'default'}>Merged</Button>
                    <Button onClick={RawrepoIntrospectRecordModeSelector.setModeExpanded}
                            bsStyle={mode === 'expanded' ? 'primary' : 'default'}>Expanded</Button>
                </ButtonGroup>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordModeSelector;