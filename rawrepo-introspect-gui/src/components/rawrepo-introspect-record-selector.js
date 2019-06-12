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
                <div className="form-group">
                    <label htmlFor="biliographic-record-id-input">BibliographicRecordId</label>
                    <input
                        type="text"
                        className="form-control"
                        id="biliographic-record-id-input"
                        onBlur={this.props.onChangeBibliographicRecordId}
                        defaultValue={this.props.bibliographicRecordId}/>

                </div>
                <div className='form-group'>
                    <label htmlFor='select-agency-id'>AgencyId</label>
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
        )
    }
}

export default RawrepoIntrospectRecordSelector;