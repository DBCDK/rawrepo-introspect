/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Tab, Tabs} from "react-bootstrap";
import RawrepoIntrospectRecordSelector from './rawrepo-introspect-record-selector';
import RawrepoIntrospectRelationsView from './rawrepo-introspect-relations-view';
import RawrepoIntrospectRecordView from "./rawrepo-introspect-record-view";

const request = require('superagent');
const queryString = require('querystring');

const COOKIE_PREFIX = 'recId=';

class RawrepoIntrospectGUI extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            view: 'record',
            bibliographicRecordId: '',
            bibliographicRecordIdCache: [],
            agencyId: '',
            agencyIdList: [],
            record: '',
            format: 'line',
            mode: 'raw',
            recordLoaded: false,
            history: [],
            version: 'current',
            relations: []
        };

        this.handleSelect = this.handleSelect.bind(this);

        this.onChangeBibliographicRecordId = this.onChangeBibliographicRecordId.bind(this);
        this.onChangeAgencyId = this.onChangeAgencyId.bind(this);
        this.onChangeMode = this.onChangeMode.bind(this);
        this.onChangeFormat = this.onChangeFormat.bind(this);
        this.onChangeVersion = this.onChangeVersion.bind(this);

        this.getAgenciesAndRefresh = this.getAgenciesAndRefresh.bind(this);
        this.getAgencies = this.getAgencies.bind(this);
        this.getRecord = this.getRecord.bind(this);
        this.getRecordById = this.getRecordById.bind(this);
        this.getRecordByMode = this.getRecordByMode.bind(this);
        this.getRecordByFormat = this.getRecordByFormat.bind(this);
        this.getRecordByVersion = this.getRecordByVersion.bind(this);
        this.getRecordByIdAndVersion = this.getRecordByIdAndVersion.bind(this);
        this.getHistory = this.getHistory.bind(this);
        this.getRelations = this.getRelations.bind(this);

        this.clearRecord = this.clearRecord.bind(this);
        this.addToCookie = this.addToCookie.bind(this);
        this.readCookie = this.readCookie.bind(this);

        RawrepoIntrospectGUI.getExpirationDate = RawrepoIntrospectGUI.getExpirationDate.bind(this);

        this.getURLParams = this.getURLParams.bind(this);
        this.setURLParams = this.setURLParams.bind(this);
    }

    componentDidMount() {
        const queryParams = this.getURLParams();

        this.readCookie();

        if (queryParams.view === undefined || queryParams.view === 'record') { // TODO implement other views
            if (queryParams.bibliographicRecordId !== undefined) {
                this.setState({bibliographicRecordId: queryParams.bibliographicRecordId})
            }

            if (queryParams.agencyId !== undefined) {
                this.setState({agencyId: queryParams.agencyId})
            }

            if (queryParams.mode !== undefined) {
                this.setState({mode: queryParams.mode})
            }

            if (queryParams.format !== undefined) {
                this.setState({format: queryParams.format})
            }

            if (queryParams.version !== undefined) {
                this.setState({version: queryParams.version})
            }

            if (queryParams.bibliographicRecordId !== undefined && queryParams.agencyId !== undefined) {
                this.getAgencies(queryParams.bibliographicRecordId);
                if (queryParams.version !== undefined) {
                    this.getRecordByIdAndVersion(queryParams.bibliographicRecordId, queryParams.agencyId, queryParams.version)
                } else {
                    this.getRecordById(queryParams.bibliographicRecordId, queryParams.agencyId);
                }
            }
        }
    }

    handleSelect(view) {
        this.setState({view: view});

        if (view === 'relations') {
            this.getRelations(this.state.bibliographicRecordId, this.state.agencyId);
        }
    }

    onChangeBibliographicRecordId(event) {
        const bibliographicRecordId = event.target.value;

        this.setState({bibliographicRecordId: bibliographicRecordId});
        if (bibliographicRecordId.length > 0) {
            this.getAgenciesAndRefresh(bibliographicRecordId);
        } else {
            this.clearRecord();
        }
    }

    clearRecord() {
        // Reset state
        this.setState({
            agencyIdList: [],
            record: '',
            recordLoaded: false,
            history: [],
            version: 'current',
            relations: []
        });

        // Reset url params
        // 'format' and 'mode' can stay but all record specific values should be cleared
        const urlParams = this.getURLParams();
        delete urlParams['bibliographicRecordId'];
        delete urlParams['agencyId'];
        delete urlParams['version'];
        this.setURLParams(urlParams);
    }

    onChangeAgencyId(event) {
        const agencyId = event.target.value;

        this.setState({agencyId: agencyId, version: 'current'});

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

    onChangeVersion(event) {
        const version = event.target.value;

        this.setState({version: version});

        this.getRecordByVersion(version);
    }

    getAgenciesAndRefresh(bibliographicRecordId) {
        request
            .get('/api/v1/agencies-for/' + bibliographicRecordId)
            .then(res => {
                const agencyIdList = res.body;

                if (agencyIdList.length > 0) {
                    const agencyId = agencyIdList[0];
                    this.setState({agencyIdList: agencyIdList, agencyId: agencyId, relations: []});
                    this.getRecordById(bibliographicRecordId, agencyId)
                } else {
                    this.clearRecord();
                }
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getAgencies(bibliographicRecordId) {
        request
            .get('/api/v1/agencies-for/' + bibliographicRecordId)
            .then(res => {
                this.setState({agencyIdList: res.body});
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getRecordByMode(mode) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const format = this.state.format;
        const version = this.state.version;

        const urlParams = this.getURLParams();
        urlParams['mode'] = mode;

        this.setURLParams(urlParams);

        this.getRecord(bibliographicRecordId, agencyId, mode, format, version);
    }


    getRecordByFormat(format) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const mode = this.state.mode;
        const version = this.state.version;

        const urlParams = this.getURLParams();
        urlParams['format'] = format;

        this.setURLParams(urlParams);

        this.getRecord(bibliographicRecordId, agencyId, mode, format, version);
    }

    getRecordByVersion(version) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const mode = this.state.mode;
        const format = this.state.format;

        const urlParams = this.getURLParams();
        urlParams['version'] = version;

        this.setURLParams(urlParams);

        this.getRecord(bibliographicRecordId, agencyId, mode, format, version);
    }

    getRecordById(bibliographicRecordId, agencyId) {
        const mode = this.state.mode;
        const format = this.state.format;
        const version = 'current';

        const urlParams = this.getURLParams();
        urlParams['bibliographicRecordId'] = bibliographicRecordId;
        urlParams['agencyId'] = agencyId;
        urlParams['version'] = version;

        this.setURLParams(urlParams);

        this.addToCookie(bibliographicRecordId);
        this.getRecord(bibliographicRecordId, agencyId, mode, format, version);
        this.getHistory(bibliographicRecordId, agencyId);
    }

    getRecordByIdAndVersion(bibliographicRecordId, agencyId, version) {
        const mode = this.state.mode;
        const format = this.state.format;

        const urlParams = this.getURLParams();
        urlParams['bibliographicRecordId'] = bibliographicRecordId;
        urlParams['agencyId'] = agencyId;
        urlParams['version'] = version;
        this.setURLParams(urlParams);

        this.addToCookie(bibliographicRecordId);
        this.getRecord(bibliographicRecordId, agencyId, mode, format, version);
        this.getHistory(bibliographicRecordId, agencyId);
    }

    getRecord(bibliographicRecordId, agencyId, mode, format, version) {
        const params = {mode: mode, format: format};

        if (version === 'current') {
            request
                .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId)
                .set('Content-Type', 'text/plain')
                .query(params)
                .then(res => {
                    this.setState({
                        record: res.text,
                        recordLoaded: true,
                        version: 'current'
                    });

                    if (this.state.view === 'relations') {
                        this.getRelations(bibliographicRecordId, agencyId);
                    }
                })
                .catch(err => {
                    alert(err.message);
                });
        } else {
            request
                .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId + '/' + version)
                .query(params)
                .then(res => {
                    this.setState({
                        record: res.text,
                        recordLoaded: true,
                        version: version
                    });
                })
                .catch(err => {
                    alert(err.message);
                });
        }
    }

    getHistory(bibliographicRecordId, agencyId) {
        request
            .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId + '/history')
            .accept('application/json')
            .then(res => {
                // We have to mark the first element as 'current' but we can't overwrite the modified date as that is the display value
                const history = res.body;
                history[0].isCurrent = true;

                this.setState({
                    history: history,
                });
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getRelations(bibliographicRecordId, agencyId) {
        request
            .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId + '/relations')
            .accept('application/json')
            .then(res => {
                const relations = res.body;

                this.setState({
                    relations: relations
                });
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getURLParams() {
        const windowLocation = window.location.search;
        const urlParamsList = windowLocation.substring(1).split('&');

        const urlParamsDict = {};

        urlParamsList.forEach(function (item, index) {
            const split = item.split('=');
            const key = split[0];
            var value = split[1];

            // Hack to url decode the date from history
            if (key === 'version' && value !== 'current') {
                value = value.replace('_', ':');
                value = value.replace('_', ':');
            }

            // Ignore weird empty key
            if (key !== "") {
                urlParamsDict[key] = value;
            }
        });

        return urlParamsDict;
    }

    setURLParams(urlParams) {
        const URL = location.protocol + '//' + location.host + location.pathname;

        // Hack to url encode the date from history.
        // Without this conversion weird stuff happens the the url when refreshing the url with the same version.
        if (urlParams.version !== undefined && urlParams.version !== 'current') {
            urlParams.version = urlParams.version.replace(':', '_');
            urlParams.version = urlParams.version.replace(':', '_');
        }

        window.history.replaceState(null, null, URL + '?' + queryString.stringify(urlParams));
    }

    // Constructs 'expires' message for cookies
    static getExpirationDate() {
        const date = new Date();
        date.setTime(date.getTime() + (7 * 24 * 60 * 60 * 1000));
        return "; expires=" + date.toGMTString();
    }

    addToCookie(recId) {
        const expires = RawrepoIntrospectGUI.getExpirationDate();
        const bibliographicRecordIdCache = this.state.bibliographicRecordIdCache;

        // Only add the value if it isn't already present
        if (bibliographicRecordIdCache.indexOf(recId) === -1) {
            bibliographicRecordIdCache.push(recId);
            this.setState({bibliographicRecordIdCache: bibliographicRecordIdCache});
            document.cookie = COOKIE_PREFIX + bibliographicRecordIdCache.join(',') + expires;
        }
    }

    readCookie() {
        let bibliographicRecordIdCache = [];
        const documentCookies = document.cookie.split(';'); // Split cookie into separate elements

        for (let i = 0; i < documentCookies.length; i++) {
            if (documentCookies[i].indexOf(COOKIE_PREFIX) > -1) {
                const bibliographicRecordIdCookie = documentCookies[i];

                // Remove 'recId='
                const bibliographicRecordIdCookieValues = bibliographicRecordIdCookie.split('=').pop();

                bibliographicRecordIdCache = bibliographicRecordIdCookieValues.split(',');
            }
        }

        this.setState({bibliographicRecordIdCache: bibliographicRecordIdCache});
    }

    render() {
        return (
            <div className="container-fluid">
                <div style={{height: '115px'}}>
                    <RawrepoIntrospectRecordSelector
                        onChangeBibliographicRecordId={this.onChangeBibliographicRecordId}
                        onChangeAgencyId={this.onChangeAgencyId}
                        bibliographicRecordId={this.state.bibliographicRecordId}
                        agencyIdList={this.state.agencyIdList}
                        agencyId={this.state.agencyId}
                        bibliographicRecordIdCache={this.state.bibliographicRecordIdCache}/>
                </div>
                <div>
                    <Tabs activeKey={this.state.view}
                          onSelect={this.handleSelect}
                          animation={false}
                          id="tabs">
                        <Tab eventKey={'record'} title="Post">
                            <div>
                                <p/>
                                <RawrepoIntrospectRecordView
                                    record={this.state.record}
                                    format={this.state.format}
                                    mode={this.state.mode}
                                    onChangeFormat={this.onChangeFormat}
                                    onChangeMode={this.onChangeMode}
                                    onSelectHistory={this.onChangeVersion}
                                    recordLoaded={this.state.recordLoaded}
                                    history={this.state.history}
                                    version={this.state.version}/>
                            </div>
                        </Tab>
                        <Tab eventKey={'relations'} title="Relationer">
                            <div><RawrepoIntrospectRelationsView
                                relations={this.state.relations}
                                onLoadRelations={this.getRelations}/></div>
                        </Tab>
                    </Tabs>
                </div>
            </div>
        )
    }
}

export default RawrepoIntrospectGUI;
