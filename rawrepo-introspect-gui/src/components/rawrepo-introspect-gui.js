/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Tab, Tabs} from "react-bootstrap";
import RawrepoIntrospectRecordSelector from './rawrepo-introspect-record-selector';
import RawrepoIntrospectRelationsView from './rawrepo-introspect-relations-view';
import RawrepoIntrospectRecordView from "./rawrepo-introspect-record-view";
import RawrepoIntrospectAttachmentView from "./rawrepo-introspect-attachment-view";
import RawrepoIntrospectHoldingsView from "./rawrepo-introspect-holdings-view";
import copy from 'copy-to-clipboard';
import fileDownload from "js-file-download";

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
            recordParts: [],
            format: 'line',
            mode: 'raw',
            recordLoaded: false,
            history: [],
            version: ['current'],
            relations: [],
            attachmentInfoDanbib: [],
            attachmentInfoUpdate: [],
            attachmentInfoBasis: [],
            instance: '',
            holdingsItemsIntrospectUrl: '',
            holdingsItems: [],
            diffEnrichment: false
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
        this.getRecordByDiffEnrichment = this.getRecordByDiffEnrichment.bind(this);
        this.getRecordByIdAndVersion = this.getRecordByIdAndVersion.bind(this);
        this.getHistory = this.getHistory.bind(this);
        this.getRelations = this.getRelations.bind(this);
        this.getInstance = this.getInstance.bind(this);
        this.getAttachmentInfoDanbib = this.getAttachmentInfoDanbib.bind(this);
        this.getAttachmentInfoUpdate = this.getAttachmentInfoUpdate.bind(this);
        this.getAttachmentInfoBasis = this.getAttachmentInfoBasis.bind(this);
        this.getHoldingsItems = this.getHoldingsItems.bind(this);

        this.onCopyRecordToClipboard = this.onCopyRecordToClipboard.bind(this);
        this.onCopyTimestampToClipboard = this.onCopyTimestampToClipboard.bind(this);
        this.onDownload = this.onDownload.bind(this);

        this.onChangeDiffEnrichment = this.onChangeDiffEnrichment.bind(this);

        this.clearRecord = this.clearRecord.bind(this);
        this.addToCookie = this.addToCookie.bind(this);
        this.readCookie = this.readCookie.bind(this);

        RawrepoIntrospectGUI.getExpirationDate = RawrepoIntrospectGUI.getExpirationDate.bind(this);
        RawrepoIntrospectGUI.formatTimestamp = RawrepoIntrospectGUI.formatTimestamp.bind(this);

        this.getURLParams = this.getURLParams.bind(this);
        this.setURLParams = this.setURLParams.bind(this);
    }

    componentDidMount() {
        const queryParams = this.getURLParams();

        this.readCookie();

        if (queryParams.view === undefined || queryParams.view === 'record') { // TODO implement other views
            let bibliographicRecordId = queryParams.bibliographicRecordId;
            if (bibliographicRecordId !== undefined) {
                this.setState({bibliographicRecordId: bibliographicRecordId})
            }

            let agencyId = queryParams.agencyId;
            if (agencyId !== undefined) {
                this.setState({agencyId: agencyId})
            }

            let mode = queryParams.mode;
            if (mode !== undefined) {
                this.setState({mode: mode})
            } else {
                mode = this.state.mode;
            }

            let format = queryParams.format;
            if (format !== undefined) {
                this.setState({format: format})
            } else {
                format = this.state.format;
            }

            let diffEnrichment = queryParams.diffEnrichment;
            if (diffEnrichment !== undefined) {
                this.setState({diffEnrichment: 'true' === diffEnrichment})
            } else {
                diffEnrichment = this.state.diffEnrichment;
            }

            let version = queryParams.version;
            if (version !== undefined) {
                this.setState({version: version})
            } else {
                version = this.state.version;
            }

            if (bibliographicRecordId !== undefined && agencyId !== undefined) {
                this.getAgencies(bibliographicRecordId);
                if (version !== undefined) {
                    this.getRecordByIdAndVersion(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version)
                } else {
                    this.getRecordById(bibliographicRecordId, agencyId);
                }
            }
        }

        if (this.state.instance === '') {
            this.getInstance();
        }


    }

    handleSelect(view) {
        this.setState({view: view});

        if (view === 'relations' && this.state.recordLoaded) {
            this.getRelations(this.state.bibliographicRecordId, this.state.agencyId);
        }

        if (view === 'attachments' && this.state.recordLoaded) {
            this.getAttachmentInfoDanbib(this.state.bibliographicRecordId);
            this.getAttachmentInfoUpdate(this.state.bibliographicRecordId);
            this.getAttachmentInfoBasis(this.state.bibliographicRecordId);
        }

        if (view === 'holdingsItems' && this.state.recordLoaded) {
            this.getHoldingsItems(this.state.bibliographicRecordId);
        }
    }

    onChangeBibliographicRecordId(event) {
        let bibliographicRecordId = event.target.value;

        bibliographicRecordId = bibliographicRecordId.replace(/\s/g, '');

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
            recordParts: [],
            recordLoaded: false,
            history: [],
            version: ['current'],
            relations: [],
            attachmentInfoDanbib: [],
            attachmentInfoUpdate: [],
            attachmentInfoBasis: [],
            holdingsItems: [],
            diffEnrichment: false
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

        this.setState({agencyId: agencyId, version: ['current']});

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

    onChangeDiffEnrichment() {
        this.setState({diffEnrichment: !this.state.diffEnrichment});

        this.getRecordByDiffEnrichment(!this.state.diffEnrichment);
    }

    onChangeVersion(event) {
        var options = event.target.options;
        var value = [];
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                value.push(options[i].value);
            }
        }

        this.setState({version: value});

        this.getRecordByVersion(value);
    }

    getAgenciesAndRefresh(bibliographicRecordId) {
        request
            .get('/api/v1/agencies-for/' + bibliographicRecordId)
            .then(res => {
                const agencyIdList = res.body;

                if (agencyIdList.length > 0) {
                    let agencyId;

                    if (agencyIdList.indexOf(191919) > -1) {
                        agencyId = 191919;
                    } else {
                        agencyId = agencyIdList[0];
                    }

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

    getInstance() {
        request
            .get('/api/v1/config')
            .set('Content-Type', 'text/plain')
            .then(res => {
                const config = res.body;
                this.setState({
                    instance: config.instance,
                    holdingsItemsIntrospectUrl: config.holdingsItemsIntrospectUrl
                });
                document.title = "Rawrepo Introspect " + config.instance;
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
        const diffEnrichment = this.state.diffEnrichment;

        const urlParams = this.getURLParams();
        urlParams['mode'] = mode;

        this.setURLParams(urlParams);

        this.getRecord(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version);
    }

    getRecordByFormat(format) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const mode = this.state.mode;
        const version = this.state.version;
        const diffEnrichment = this.state.diffEnrichment;

        const urlParams = this.getURLParams();
        urlParams['format'] = format;

        this.setURLParams(urlParams);

        this.getRecord(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version);
    }

    getRecordByVersion(version) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const mode = this.state.mode;
        const format = this.state.format;
        const diffEnrichment = this.state.diffEnrichment;

        const urlParams = this.getURLParams();
        urlParams['version'] = version;

        this.setURLParams(urlParams);

        this.getRecord(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version);
    }

    getRecordById(bibliographicRecordId, agencyId) {
        const mode = this.state.mode;
        const format = this.state.format;
        const diffEnrichment = this.state.diffEnrichment;
        const version = ['current'];

        const urlParams = this.getURLParams();
        urlParams['bibliographicRecordId'] = bibliographicRecordId;
        urlParams['agencyId'] = agencyId;
        urlParams['version'] = version;

        this.setURLParams(urlParams);

        this.addToCookie(bibliographicRecordId);
        this.getRecord(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version);
        this.getHistory(bibliographicRecordId, agencyId);
    }

    getRecordByIdAndVersion(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version) {
        const urlParams = this.getURLParams();
        urlParams['bibliographicRecordId'] = bibliographicRecordId;
        urlParams['agencyId'] = agencyId;
        urlParams['version'] = version;
        urlParams['diffEnrichment'] = diffEnrichment;
        this.setURLParams(urlParams);

        this.addToCookie(bibliographicRecordId);
        this.getRecord(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version);
        this.getHistory(bibliographicRecordId, agencyId);
    }

    getRecordByDiffEnrichment(diffEnrichment) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const mode = this.state.mode;
        const format = this.state.format;
        const version = this.state.version;

        const urlParams = this.getURLParams();
        urlParams['diffEnrichment'] = diffEnrichment;

        this.setURLParams(urlParams);

        this.getRecord(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version);
    }

    getRecord(bibliographicRecordId, agencyId, mode, format, diffEnrichment, version) {
        const params = {mode: mode, format: format, diffEnrichment: diffEnrichment};

        if (version.length === 0) {
            this.setState({
                recordParts: [],
                recordLoaded: true
            });
        } else if (version.length === 1 && version[0] === 'current') {
            // Load current version
            request
                .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId)
                .query(params)
                .then(res => {
                    this.setState({
                        recordParts: res.body.recordParts,
                        recordLoaded: true,
                        version: ['current']
                    });

                    if (this.state.view === 'relations') {
                        this.getRelations(bibliographicRecordId, agencyId);
                    }

                    if (this.state.view === 'attachments') {
                        this.getAttachmentInfoDanbib(bibliographicRecordId);
                        this.getAttachmentInfoUpdate(bibliographicRecordId);
                        this.getAttachmentInfoBasis(bibliographicRecordId);
                    }

                    if (this.state.view === 'holdingsItems') {
                        this.getHoldingsItems(bibliographicRecordId);
                    }
                })
                .catch(err => {
                    alert(err.message);
                });
        } else if (version.length === 1) {
            // Load historic version
            request
                .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId + '/' + version[0])
                .query(params)
                .then(res => {
                    this.setState({
                        recordParts: res.body.recordParts,
                        recordLoaded: true,
                        version: version
                    });
                })
                .catch(err => {
                    alert(err.message);
                });
        } else if (version.length === 2) {
            params.format = format;
            // Load record diff
            request
                .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId + '/diff/' + version.join('|'))
                .query(params)
                .then(res => {
                    this.setState({
                        recordParts: res.body.recordParts,
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

    getAttachmentInfoDanbib(bibliographicRecordId) {
        request
            .get('/api/v1/attachment/danbib/' + bibliographicRecordId)
            .accept('application/json')
            .then(res => {
                this.setState({
                    attachmentInfoDanbib: res.body
                });
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getAttachmentInfoUpdate(bibliographicRecordId) {
        request
            .get('/api/v1/attachment/update/' + bibliographicRecordId)
            .accept('application/json')
            .then(res => {
                this.setState({
                    attachmentInfoUpdate: res.body
                });
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getAttachmentInfoBasis(bibliographicRecordId) {
        request
            .get('/api/v1/attachment/basis/' + bibliographicRecordId)
            .accept('application/json')
            .then(res => {
                this.setState({
                    attachmentInfoBasis: res.body
                });
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getHoldingsItems(bibliographicRecordId) {
        request
            .get('/api/v1/holdingsitems/' + bibliographicRecordId)
            .accept('application/json')
            .then(res => {
                this.setState({
                    holdingsItems: res.body
                });
            })
            .catch(err => {
                alert(err.message);
            });
    }

    onCopyRecordToClipboard(e) {
        let text = '';
        this.state.recordParts.map((item, key) => {
                text = text + (item.content);
            }
        );

        copy(text);
    }

    onCopyTimestampToClipboard() {
        if (this.state.version[0] === 'current') {
            copy(RawrepoIntrospectGUI.formatTimestamp(this.state.history[0].modified));
        } else {
            copy(RawrepoIntrospectGUI.formatTimestamp(this.state.version[0]));
        }
    }

    onDownload() {
        let text = '';
        this.state.recordParts.map((item, key) => {
                if (item.type === 'right') {
                    text += '-' + (item.content);
                } else if (item.type === 'left') {
                    text += '+' + (item.content);
                } else {
                    text = text + (item.content);
                }
            }
        );

        let fileName = this.state.bibliographicRecordId + '_' + this.state.agencyId;

        if (this.state.version.length === 1) {
            if (this.state.version[0] !== 'current') {
                fileName += '_' + this.state.version[0];
            }
        } else if (this.state.version.length === 2) {
            fileName += '_diff_' + this.state.version[0] + '_' + this.state.version[1]
        }

        if (this.state.format === 'line') {
            fileName += '.txt'
        } else {
            fileName += '.xml'
        }

        fileDownload(text, fileName);
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
            if (key === 'version') {
                value = decodeURIComponent(value); // The queryString is implicit url encoded so we have to decode it first
                value = value.split(',');

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

        if (urlParams.version !== undefined) {
            urlParams.version = urlParams.version.join(',');
        }

        window.history.replaceState(null, null, URL + '?' + queryString.stringify(urlParams));
    }

    static formatTimestamp(date) {
        // The date is in gmt+0 timezone, but we need it in local/Danish timezone, plus we need the milliseconds as well
        let dateValue = new Date(date);

        // Used for making date and time segments two chars long.
        let leftPad2 = function (val) {
            return ("00" + val).slice(-2)
        };

        let leftPad3 = function (val) {
            return ("000" + val).slice(-3)
        };

        return dateValue.getFullYear() +
            '-' + leftPad2(dateValue.getMonth() + 1) +
            '-' + leftPad2(dateValue.getDate()) +
            ' ' + leftPad2(dateValue.getHours()) +
            ':' + leftPad2(dateValue.getMinutes()) +
            ':' + leftPad2(dateValue.getSeconds()) +
            '.' + leftPad3(dateValue.getMilliseconds());
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
                        bibliographicRecordIdCache={this.state.bibliographicRecordIdCache}
                        instance={this.state.instance}/>
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
                                    recordParts={this.state.recordParts}
                                    format={this.state.format}
                                    mode={this.state.mode}
                                    onChangeFormat={this.onChangeFormat}
                                    onChangeMode={this.onChangeMode}
                                    onChangeVersion={this.onChangeVersion}
                                    onCopyRecordToClipboard={this.onCopyRecordToClipboard}
                                    onCopyTimestampToClipboard={this.onCopyTimestampToClipboard}
                                    onDownload={this.onDownload}
                                    onChangeDiffEnrichment={this.onChangeDiffEnrichment}
                                    recordLoaded={this.state.recordLoaded}
                                    history={this.state.history}
                                    version={this.state.version}
                                    diffEnrichment={this.state.diffEnrichment}
                                />
                            </div>
                        </Tab>
                        <Tab eventKey={'relations'} title="Relationer">
                            <div><RawrepoIntrospectRelationsView
                                relations={this.state.relations}
                                onLoadRelations={this.getRelations}
                                bibliographicRecordId={this.state.bibliographicRecordId}
                                agencyId={this.state.agencyId}/></div>
                        </Tab>
                        <Tab eventKey={'attachments'} title="Attachments">
                            <div><RawrepoIntrospectAttachmentView
                                attachmentInfoDanbib={this.state.attachmentInfoDanbib}
                                attachmentInfoUpdate={this.state.attachmentInfoUpdate}
                                attachmentInfoBasis={this.state.attachmentInfoBasis}
                                bibliographicRecordId={this.state.bibliographicRecordId}/>
                            </div>
                        </Tab>
                        <Tab eventKey={'holdingsItems'} title="Beholdninger">
                            <div><RawrepoIntrospectHoldingsView
                                holdingsItems={this.state.holdingsItems}
                                bibliographicRecordId={this.state.bibliographicRecordId}
                                holdingsItemsIntrospectUrl={this.state.holdingsItemsIntrospectUrl}/>
                            </div>
                        </Tab>
                    </Tabs>
                </div>
            </div>
        )
    }
}

export default RawrepoIntrospectGUI;
