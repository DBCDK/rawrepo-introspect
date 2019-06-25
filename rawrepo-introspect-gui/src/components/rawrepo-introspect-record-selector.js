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
                    <input
                        type="text"
                        className="form-control"
                        id="biliographic-record-id-input"
                        onChange={this.props.onChangeBibliographicRecordId}
                        value={this.props.bibliographicRecordId}
                        style={{width: '250px'}}/>
                </div>
                <div className='form-group'>
                    <div>
                        <select className='form-control'
                                id='select-agency-id'
                                onChange={this.props.onChangeAgencyId}
                                value={this.props.agencyId}
                                style={{width: '250px'}}>
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