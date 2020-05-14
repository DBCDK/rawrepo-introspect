/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

class RawrepoIntrospectDownload extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <div id='download-div'>
                    <ButtonGroup id='button-tool-bar-download'>
                        <Button onClick={this.props.onDownload}
                                bsStyle='default'
                                id='button-download'
                                disabled={!this.props.recordLoaded}>Download</Button>
                    </ButtonGroup>
                </div>
            </div>
        )
    }
}

export default RawrepoIntrospectDownload;