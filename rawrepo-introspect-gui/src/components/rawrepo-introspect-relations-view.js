/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import ReactDOM from "react-dom";
import dagreD3 from "dagre-d3";
import * as d3 from 'd3'

let g = new dagreD3.graphlib.Graph().setGraph({});

const HEIGHT_OFFSET = 175;
const WIDTH_OFFSET = 20;

class RawrepoIntrospectRelationsView extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            nodes: [],
            edges: [],
            height: window.innerHeight - HEIGHT_OFFSET,
            width: window.innerWidth - WIDTH_OFFSET,
        };

        this.resetGraph = this.resetGraph.bind(this);
        this.drawGraph = this.drawGraph.bind(this);
        this.updateDimensions = this.updateDimensions.bind(this);
    }

    componentWillMount() {
        this.updateDimensions();
    };

    componentDidMount() {
        window.addEventListener("resize", this.updateDimensions);
    };

    componentWillUnmount() {
        window.removeEventListener("resize", this.updateDimensions);
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.relations !== undefined) {
            const relations = nextProps.relations;

            if (relations.length === 0) {
                this.resetGraph();
            } else {
                let hasChanges = false;

                if (relations.nodes !== this.state.nodes) {
                    this.setState({nodes: relations.nodes});
                    hasChanges = true;
                }

                if (relations.edges !== this.state.edges) {
                    this.setState({edges: relations.edges});
                    hasChanges = true;
                }

                if (hasChanges) {
                    this.drawGraph(relations.nodes, relations.edges, nextProps.onLoadRelations, nextProps.bibliographicRecordId, nextProps.agencyId);
                }
            }
        }
    }

    updateDimensions() {
        this.setState({
            height: window.innerHeight - HEIGHT_OFFSET,
            width: window.innerWidth - WIDTH_OFFSET,
        });
    };

    resetGraph() {
        g = new dagreD3.graphlib.Graph().setGraph({});

        const render = new dagreD3.render();
        const svgGroup = d3.select(ReactDOM.findDOMNode(this.refs.nodeTreeGroup));

        render(svgGroup, g);
    }

    drawGraph(nodes, edges, callBack, bibliographicRecordId, agencyId) {
        nodes.forEach(function (item) {
            const label = item.bibliographicRecordId + ':' + item.agencyId;
            const url = '?bibliographicRecordId=' + item.bibliographicRecordId + '&agencyId=' + item.agencyId;
            const value = {
                labelType: "html",
                // TODO Make the a href more react like
                //label: <a href={{url}} target='_blank'>{{label}}</a>,
                label: "<a href='?bibliographicRecordId=" + item.bibliographicRecordId + "&agencyId=" + item.agencyId + "' target='_blank'>" + label + "</a>",
                rx: 5, // Curved corners
                ry: 5,
                callBack: callBack,
                bibliographicRecordId: item.bibliographicRecordId,
                agencyId: item.agencyId,
                style: (bibliographicRecordId === item.bibliographicRecordId && agencyId.toString() === item.agencyId.toString()) ? "fill: #afa" : "fill: #9cbedb"
            };

            g.setNode(label, value);
        });

        edges.forEach(function (item) {
            const childLabel = item.child.bibliographicRecordId + ':' + item.child.agencyId;
            const parentLabel = item.parent.bibliographicRecordId + ':' + item.parent.agencyId;

            g.setEdge(childLabel, parentLabel, {})
        });

        g.graph().rankdir = "BT"; //orientation (TB, RL, BT, LR)
        g.graph().nodesep = 30; //defines how far apart nodes are by pixels -- horizontal
        g.graph().edgesep = 10; //Number of pixels that separate edges horizontally in the layout.
        g.graph().ranksep = 50;//Number of pixels between each rank in the layout. default 50
        //g.graph().align = "UL"; //Alignment for rank nodes. Can be UL, UR, DL, or DR, where U = up, D = down, L = left, and R = right. 	Default is undefined and centered
        g.graph().ranker = "network-simplex"; //Type of algorithm to assigns a rank to each node in the input graph. Possible values: network-simplex, tight-tre

        // Create the renderer
        const render = new dagreD3.render();

        // Set up an SVG group so that we can translate the final graph.
        const svg = d3.select(ReactDOM.findDOMNode(this.refs.nodeTree));
        const svgGroup = d3.select(ReactDOM.findDOMNode(this.refs.nodeTreeGroup));

        const zoom = d3.zoom()
            .on("zoom", function () {
                svgGroup.attr("transform", d3.event.transform);
            });
        svg.call(zoom);

        // Run the renderer. This is what draws the final graph.
        render(svgGroup, g);

        // Add onClick event handler to each node
        svgGroup.selectAll("g.node")
            .on("click", function (v) {
                const node = g.node(v);
                node.callBack(node.bibliographicRecordId, node.agencyId);
            });

        // Center the graph
        const xCenterOffset = (svg.attr("width") - g.graph().width) / 2;
        svgGroup.attr("transform", "translate(" + xCenterOffset + ", 20)");

        const initialScale = 1.5;
        svg.call(zoom.transform, d3.zoomIdentity.translate((svg.attr("width") - g.graph().width) / 2, 30).scale(initialScale));
    }

    render() {
        return (
            <div>
                <svg id="nodeTree" ref="nodeTree" width={this.state.width} height={this.state.height}>
                    <g ref="nodeTreeGroup"/>
                </svg>
            </div>
        )
    }
}

export default RawrepoIntrospectRelationsView;
