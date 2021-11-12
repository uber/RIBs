import React, { Component } from 'react'
import * as d3 from "d3";

var searchTerm = '';
var activeNodeId = '';

class TreeChart extends Component {

   constructor(props) {
       super(props);
       this.renderTree = this.renderTree.bind(this);
   }

   componentDidMount() {
      this.renderTree();
   }

   componentDidUpdate() {
     this.renderTree();
   }

   getTextOpacity(d) {
     if (!searchTerm || d.data.name.toLowerCase().includes(searchTerm)) {
        return 1;
     }
     return 0.1;
   }

   renderTree() {
        searchTerm = this.props.searchTerm.toLowerCase();
        activeNodeId = this.props.activeNodeId;

        var data = this.props.data;
        var diagonal = d3.linkHorizontal().x(function(d) { return d.y; }).y(function(d) { return d.x; });

       var innerWidth = this.props.width - this.props.margin.right - this.props.margin.left;
       var innerHeight = this.props.height - this.props.margin.top - this.props.margin.bottom;
       var marginRight = this.props.margin.right;
       this.tree = d3.tree().size([innerWidth, innerHeight]);

       var treeRoot = d3.hierarchy(this.props.data[0]);
       this.tree(treeRoot);

       var nodes = treeRoot.descendants().reverse();
       var links = treeRoot.links();

       var maxDepth = d3.max(nodes, d => d.depth);
       var maxWidth = innerWidth;
       var marginLeft = this.props.margin.left;
       nodes.forEach(function(d) { d.y = (d.depth * maxWidth) / maxDepth + marginLeft});

       var maxHeight = d3.max(nodes, d => d.y);
       var windowHeight = innerHeight;
       nodes.forEach(function(d) { d.x = (d.x * windowHeight) / maxHeight });

       var svg = d3.select(this.node);
       var node = svg.selectAll("g.node").data(nodes, function(d) { return d.data.id });

       var nodeEnter = node.enter().append("g")
         .attr("class", "node")
         .attr("transform", function(d) {
           var parentX = d.parent ? d.parent.x : d.x;
           var parentY = d.parent ? d.parent.y : d.y;
           return "translate(" + parentY + "," + parentX + ")"; })
         .on("click", this.props.onNodeClick)
         .on("mouseover", this.props.onNodeMouseOver)
         .on("mouseout", this.props.onNodeMouseOut);

       nodeEnter.append("circle")
         .attr("r", 0)
         .style("stroke-width", "2px")
         .style("stroke", function(d) { return d.data.hasView ? "#fbb" : "steelblue" })
         .style("fill", "#fff");

       nodeEnter.append("text")
         .attr("x", function(d) { return d.depth == maxDepth ? marginRight : 0; })
         .attr("y", "-10")
         .style("font-size", this.textStyle)
         .attr("text-anchor", function(d) { return d.depth == maxDepth ? "end" : "middle"; })
         .text(function(d) { return d.data.name; })
         .style("fill-opacity", 0);

       var nodeEnterUpdate = nodeEnter.transition()
         .duration(this.props.duration)
         .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });
       nodeEnterUpdate.select("circle").attr("r", 6);
       nodeEnterUpdate.select("text").style("fill-opacity", this.getTextOpacity);

       var nodeUpdate = node.transition()
         .duration(this.props.duration)
         .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
         .select("text").style("fill-opacity", this.getTextOpacity);

      var nodeUpdate = node.select("circle")
         .style("fill", function(d) { return d.data.id == activeNodeId ? (d.data.hasView ? "#fdd" : "#6ac") : "#fff" });

       var nodeExit = node.exit().transition()
          .duration(this.props.duration)
          .remove();
       nodeExit.select("circle").attr("r", 0);
       nodeExit.select("text").style("fill-opacity", 0);

       var link = svg.selectAll("path.link").data(links, function(d) { return d.target.data.id; });

       link.enter().insert("path", "g")
          .attr("class", "link")
          .style("fill", "none")
          .style("stroke", "#ccc")
          .style("stroke-width", "1px")
          .attr("d", function(d) {
            var x = d.source && d.source.x ? d.source.x : innerHeight / 2;
            var y = d.source && d.source.y ? d.source.y : 0;
            var o = {x, y};
            return diagonal({source: o, target: o});
          })
          .transition()
            .duration(this.props.duration)
            .attr("d", diagonal);

       link.transition()
            .duration(this.props.duration)
            .attr("d", diagonal);

       link.exit().remove();
   }

   render() {
      return <svg ref={node => this.node = node} width={this.props.width} height={this.props.height}></svg>;
   }
}
export default TreeChart
