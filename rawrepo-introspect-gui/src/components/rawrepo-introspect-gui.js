/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Tab, Tabs} from "react-bootstrap";
import RawrepoIntrospectRecordSelector from './rawrepo-introspect-record-selector';
import RawrepoIntrospectRecordView from './rawrepo-introspect-record-view';
import RawrepoIntrospectRelationsView from './rawrepo-introspect-relations-view';

const request = require('superagent');
const queryString = require('querystring');

class RawrepoIntrospectGUI extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            view: 'record',
            bibliographicRecordId: null,
            agencyId: undefined,
            agencyIdList: [],
            record: '',
            format: 'line',
            mode: 'merged'
        };

        this.handleSelect = this.handleSelect.bind(this);

        this.onChangeBibliographicRecordId = this.onChangeBibliographicRecordId.bind(this);
        this.onChangeAgencyId = this.onChangeAgencyId.bind(this);
        this.onChangeMode = this.onChangeMode.bind(this);
        this.onChangeFormat = this.onChangeFormat.bind(this);

        this.findAgenciesForBibliographicRecordId = this.findAgenciesForBibliographicRecordId.bind(this);
        this.getRecord = this.getRecord.bind(this);
        this.getRecordById = this.getRecordById.bind(this);
        this.getRecordByMode = this.getRecordByMode.bind(this);
        this.getRecordByFormat = this.getRecordByFormat.bind(this);
    }

    componentDidMount() {
        const queryParams = queryString.parse(location.search);

        if (queryParams.view === undefined) {
            console.log("View missing - setting default");
            queryParams.view = 'record';
            location.search = queryString.stringify(queryParams);
        }

        if (queryParams.bibliographicRecordId !== undefined) {
            this.findAgenciesForBibliographicRecordId(queryParams.bibliographicRecordId);
        }

        if (queryParams.bibliographicRecordId !== undefined && queryParams.agencyId !== undefined) {
            this.getRecord(queryParams.bibliographicRecordId, queryParams.agencyId);
        }
    }

    handleSelect(view) {
        this.setState({view: view});
    }

    onChangeBibliographicRecordId(event) {
        const bibliographicRecordId = event.target.value;

        this.setState({bibliographicRecordId: bibliographicRecordId});

        this.findAgenciesForBibliographicRecordId(bibliographicRecordId);
    }

    onChangeAgencyId(event) {
        const agencyId = event.target.value;

        this.setState({agencyId: agencyId});

        this.getRecordById(this.state.bibliographicRecordId, agencyId);
    }

    onChangeMode(event) {
        const mode = event.target.value;

        this.setState({mode: mode});

        this.getRecordByMode(mode);
    }

    onChangeFormat(event) {
        const format = event.target.value;

        this.setState({format: format});

        this.getRecordByFormat(format);
    }

    findAgenciesForBibliographicRecordId(bibliographicRecordId) {
        if (8 <= bibliographicRecordId.length && 9 >= bibliographicRecordId.length) {
            request
                .get('/api/v1/agencies-for/' + bibliographicRecordId)
                .then(res => {
                    console.log(res.body);
                    this.setState({agencyIdList: res.body});
                })
                .catch(err => {
                    alert(err.message);
                });
        } else {
            this.setState({
                agencyIdList: []
            })
        }
    }

    getRecordByMode(mode) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const format = this.state.format;

        this.getRecord(bibliographicRecordId, agencyId, mode, format);
    }

    getRecordByFormat(format) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const mode = this.state.mode;

        this.getRecord(bibliographicRecordId, agencyId, mode, format);
    }

    getRecordById(bibliographicRecordId, agencyId) {
        const mode = this.state.mode;
        const format = this.state.format;

        this.getRecord(bibliographicRecordId, agencyId, mode, format);
    }

    getRecord(bibliographicRecordId, agencyId, mode, format) {
        const params = {mode: mode, format: format};

        request
            .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId)
            .set('Content-Type', 'text/plain')
            .query(params)
            .then(res => {
                this.setState({
                    record: res.text
                });
            })
            .catch(err => {
                alert(err.message);
            });
    }

    render() {
        return (
            <div className="container-fluid">
                <h2>Rawrepo Introspect</h2>
                <hr/>
                <div><RawrepoIntrospectRecordSelector
                    onChangeBibliographicRecordId={this.onChangeBibliographicRecordId}
                    onChangeAgencyId={this.onChangeAgencyId}
                    bibliographicRecordId={this.state.bibliographicRecordId}
                    agencyIdList={this.state.agencyIdList}
                    agencyId={this.state.agencyId}/></div>
                <div>
                    <Tabs activeKey={this.state.view}
                          onSelect={this.handleSelect}
                          animation={false}
                          id="tabs">
                        <Tab eventKey={'record'} title="Post">
                            <div><p/><RawrepoIntrospectRecordView
                                record={this.state.record}
                                format={this.state.format}
                                mode={this.state.mode}
                                onChangeFormat={this.onChangeFormat}
                                onChangeMode={this.onChangeMode}/></div>
                        </Tab>
                        <Tab eventKey={'relations'} title="Relationer">
                            <div><p/><RawrepoIntrospectRelationsView/></div>
                        </Tab>
                    </Tabs>
                </div>
            </div>
        )
    }

}

export default RawrepoIntrospectGUI;
