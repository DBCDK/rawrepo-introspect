/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

class RawrepoIntrospectRecordCopy extends React.Component {

    constructor(props) {
        super(props);
    }


    render() {
        return (
            <div>
                <div id='format-div'>
                    <ButtonGroup id='button-tool-bar-format'>
                        <Button onClick={this.props.onCopyToClipboard}
                                bsStyle='default'
                                id='button-copy'
                                disabled={!this.props.recordLoaded}>Kopiér post</Button>
                    </ButtonGroup>
                </div>
            </div>
        )
    }
}

export default RawrepoIntrospectRecordCopy;