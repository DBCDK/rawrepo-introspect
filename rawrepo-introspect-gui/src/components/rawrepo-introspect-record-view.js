/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import RawrepoIntrospectRecordModeSelector from './rawrepo-introspect-record-mode-selector';
import RawrepoIntrospectRecordFormatSelector from './rawrepo-introspect-record-format-selector';

class RawrepoIntrospectRecordView extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            textareaHeight: window.innerHeight - 200,
            textareaWidth: window.innerWidth * 0.80
        };

        this.updateDimensions = this.updateDimensions.bind(this);
    }

    updateDimensions() {
        this.setState({
            textareaHeight: window.innerHeight - 250,
            textareaWidth: window.innerWidth - 300 // 300 reserved for future record history box
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
                                recordLoaded={this.props.recordLoaded}/>
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
                    <textarea style={{height: this.state.textareaHeight + 'px', width: this.state.textareaWidth + 'px'}}
                              value={this.props.record} readOnly/>
                </div>
            </div>
        )
    }
}

export default RawrepoIntrospectRecordView;
