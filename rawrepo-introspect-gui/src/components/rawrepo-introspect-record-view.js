/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import RawrepoIntrospectRecordModeSelector from './rawrepo-introspect-record-mode-selector';
import RawrepoIntrospectRecordFormatSelector from './rawrepo-introspect-record-format-selector';
import RawrepoIntrospectRecordCopy from './rawrepo-introspect-record-copy';

const HEIGHT_OFFSET = 225;
const HISTORY_WIDTH = 200;

class RawrepoIntrospectRecordView extends React.Component {


    constructor(props) {
        super(props);

        this.state = {
            textareaHeight: window.innerHeight - HEIGHT_OFFSET,
            recordWidth: window.innerWidth - HISTORY_WIDTH - 60,
            historyWidth: HISTORY_WIDTH
        };

        this.updateDimensions = this.updateDimensions.bind(this);
    }

    updateDimensions() {
        this.setState({
            textareaHeight: window.innerHeight - HEIGHT_OFFSET,
            recordWidth: window.innerWidth - HISTORY_WIDTH - 60
        });
    };

    componentWillMount() {
        this.updateDimensions();
    };

    componentDidMount() {
        window.addEventListener("resize", this.updateDimensions);
    };

    componentWillUnmount() {
        window.removeEventListener("resize", this.updateDimensions);
    }

    // Formats the raw ISO date to make it more friendly to look at
    static formatHistoryDate(date) {
        let dateValue = new Date(date);

        // Used for making date and time segments two chars long.
        let leftPad2 = function (val) {
            return ("00" + val).slice(-2)
        };

        return dateValue.getFullYear() +
            '-' + leftPad2(dateValue.getMonth() + 1) +
            '-' + leftPad2(dateValue.getDate()) +
            ' ' + leftPad2(dateValue.getHours()) +
            ':' + leftPad2(dateValue.getMinutes()) +
            ':' + leftPad2(dateValue.getSeconds());
    }

    static formatToolTip(item) {
        let toolTipText = item.deleted ? 'Slettet' : 'Aktiv';
        toolTipText += ' | ';
        toolTipText += item.mimeType;

        return toolTipText;
    }

    render() {
        return (
            <div>
                <div style={{width: '100%', overflow: 'hidden'}}>
                    <div className='form-group' style={{height: '28px'}}>
                        <label
                            className='control-label'
                            style={{marginTop: '5px', float: 'left'}}
                            htmlFor='record-mode-selector'>Visningstype</label>
                        <div style={{marginLeft: '10px', float: 'left', width: '250px'}}>
                            <RawrepoIntrospectRecordModeSelector
                                id='record-mode-selector'
                                mode={this.props.mode}
                                onChangeMode={this.props.onChangeMode}
                                recordLoaded={this.props.recordLoaded}
                                version={this.props.version}/>
                        </div>
                        <label
                            className='control-label'
                            style={{marginTop: '5px', float: 'left'}}
                            htmlFor='record-format-selector'>Visningsformat</label>
                        <div style={{marginLeft: '10px', float: 'left'}}>
                            <RawrepoIntrospectRecordFormatSelector
                                id='record-format-selector'
                                format={this.props.format}
                                onChangeFormat={this.props.onChangeFormat}
                                recordLoaded={this.props.recordLoaded}/>
                        </div>
                        <div style={{marginLeft: '25px', float: 'left'}}>
                            <RawrepoIntrospectRecordCopy
                                onCopyToClipboard={this.props.onCopyToClipboard}
                                recordLoaded={this.props.recordLoaded}/>
                        </div>
                    </div>
                </div>
                <div id="content-container"
                     style={{
                         height: this.state.textareaHeight + 'px',
                         width: this.state.recordWidth + 'px'
                     }}>
                    <div id="content">
                        {this.props.recordParts.map((item, key) =>
                            <span
                                key={key}
                                className={item.type}>
                                {item.content}
                            </span>
                        )}
                    </div>
                </div>
                <div>
                    <select
                        style={{
                            height: this.state.textareaHeight + 'px',
                            width: this.state.historyWidth + 'px',
                            float: 'right',
                            border: '1px solid'
                        }}
                        name="history-list"
                        multiple={true}
                        onChange={this.props.onChangeVersion}
                        value={this.props.version}>
                        {this.props.history.map((item, key) =>
                            <option
                                key={key}
                                style={{color: item.deleted === true ? 'red' : 'black'}}
                                title={RawrepoIntrospectRecordView.formatToolTip(item)}
                                value={item.isCurrent !== undefined ? 'current' : item.modified}>
                                {RawrepoIntrospectRecordView.formatHistoryDate(item.modified)}
                            </option>
                        )}
                    </select>
                </div>
            </div>
        )
    }
}

export default RawrepoIntrospectRecordView;
