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
                            placeholder='BibliographicRecordId'
                            list='BibliographicRecordIdCache'/>
                        <datalist id="BibliographicRecordIdCache">
                            {this.props.bibliographicRecordIdCache.map((item, key) =>
                                <option value={item} key={key}/>
                            )}
                        </datalist>
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
                    <h2 style={{marginTop: '15px'}}>Rawrepo <b>{this.props.instance}</b></h2>
                </div>
                <div style={{marginLeft: '260px'}}>
                    <p style={{marginTop: '22px'}}><b>{this.props.agencyIdList.length} agencies fundet</b></p>
                </div>
            </div>

        )
    }
}

export default RawrepoIntrospectRecordSelector;