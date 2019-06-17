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
                <div>
                    <div className='form-group' style={{height: '28px'}}>
                        <label
                            className='control-label col-md-1'
                            style={{top: '7px'}}
                            htmlFor='record-mode-selector'>Visningstype</label>
                        <div className='col-md-3'>
                            <RawrepoIntrospectRecordModeSelector
                                id='record-mode-selector'/>
                        </div>
                        <label
                            className='control-label col-md-1'
                            style={{top: '7px'}}
                            htmlFor='record-format-selector'>Visningsformat</label>
                        <div className='col-md-4'>
                            <RawrepoIntrospectRecordFormatSelector
                                id='record-format-selector'/>
                        </div>
                    </div>
                </div>
                <div>
                    <textarea rows='20' cols='200' value={this.props.record} readOnly/>
                </div>
            </div>
        )
    }
}

export default RawrepoIntrospectRecordView;
