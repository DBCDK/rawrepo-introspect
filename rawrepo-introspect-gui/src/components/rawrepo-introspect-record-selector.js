/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";

class RawrepoIntrospectRecordSelector extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div style={{width: '100%', overflow: 'hidden'}}>
                <div style={{float: 'left', marginTop: '15px', width: '250px'}}>
                    <div className="form-group">
                        <input
                            type="text"
                            className="form-control"
                            id="biliographic-record-id-input"
                            onChange={this.props.onChangeBibliographicRecordId}
                            value={this.props.bibliographicRecordId}
                            placeholder='BibliographicRecordId'/>
                    </div>
                    <div className='form-group'>
                        <div>
                            <select className='form-control'
                                    id='select-agency-id'
                                    onChange={this.props.onChangeAgencyId}
                                    value={this.props.agencyId}>
                                {this.props.agencyIdList.map((item, key) =>
                                    <option item={item} key={key}>{item}</option>
                                )}
                            </select>
                        </div>
                    </div>
                </div>
                <div style={{marginLeft: '260px'}}>
                    <h1 style={{marginTop: '10px'}}>Rawrepo Introspect</h1>
                </div>
            </div>

        )
    }
}

export default RawrepoIntrospectRecordSelector;