/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import RawrepoIntrospectRecordModeSelector from './rawrepo-introspect-record-mode-selector';
import RawrepoIntrospectRecordFormatSelector from './rawrepo-introspect-record-format-selector';

class RawrepoIntrospectRecordView extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <RawrepoIntrospectRecordModeSelector/>
                <hr/>
                <RawrepoIntrospectRecordFormatSelector/>
                <hr/>
                <textarea rows='20' cols='200' value={this.props.record} readOnly/>
            </div>
        )
    }
}

export default RawrepoIntrospectRecordView;
