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
            <div>
                <div className='col-lg-2'>
                    <div className="form-group" style={{marginTop: '15px'}}>
                        <input
                            type="text"
                            className="form-control"
                            id="biliographic-record-id-input"
                            onChange={this.props.onChangeBibliographicRecordId}
                            value={this.props.bibliographicRecordId}/>
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
                <div className='col-lg-10'>
                    <h1>Rawrepo Introspect</h1>
                </div>
            </div>

        )
    }
}

export default RawrepoIntrospectRecordSelector;