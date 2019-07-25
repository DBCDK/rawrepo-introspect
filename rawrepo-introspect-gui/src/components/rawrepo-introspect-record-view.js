/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import RawrepoIntrospectRecordModeSelector from './rawrepo-introspect-record-mode-selector';
import RawrepoIntrospectRecordFormatSelector from './rawrepo-introspect-record-format-selector';

const HEIGHT_OFFSET = 225;
const HISTORY_WIDTH = 200;

class RawrepoIntrospectRecordView extends React.Component {


    constructor(props) {
        super(props);

        this.state = {
            textareaHeight: window.innerHeight - HEIGHT_OFFSET,
            recordWidth: window.innerWidth - HISTORY_WIDTH - 50, // 50 px extra offset seems to make everything work
            historyWidth: HISTORY_WIDTH
        };

        this.updateDimensions = this.updateDimensions.bind(this);
    }

    updateDimensions() {
        this.setState({
            textareaHeight: window.innerHeight - HEIGHT_OFFSET,
            recordWidth: window.innerWidth - HISTORY_WIDTH - 50
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
        date = date.replace('T', ' ');
        date = date.replace('Z', '');

        return date
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
                    </div>
                </div>
                <div>
                    <textarea
                        style={{
                            height: this.state.textareaHeight + 'px',
                            width: this.state.recordWidth + 'px',
                            float: 'left'
                        }}
                        value={this.props.record}
                        readOnly/>
                </div>
                <div>
                    <select
                        style={{
                            height: this.state.textareaHeight + 'px',
                            width: this.state.historyWidth + 'px',
                            marginLeft: '15px',
                            float: 'right'
                        }}
                        name="history-list"
                        multiple
                        onChange={this.props.onSelectHistory}
                        value={[this.props.version]}>
                        {this.props.history.map((item, key) =>
                            <option
                                key={key}
                                style={{color: item.deleted === true ? 'red' : 'black'}}

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
