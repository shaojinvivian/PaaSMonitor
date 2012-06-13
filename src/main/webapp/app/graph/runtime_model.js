
function constructToolbar(toolbar, graph) {
	var exportbutton = document.createElement('button');
	mxUtils.write(exportbutton, 'export');
	mxEvent.addListener(exportbutton, 'click', function(evt) {
		var textarea = document.createElement('textarea');
		textarea.style.width = '400px';
		textarea.style.height = '400px';
		var enc = new mxCodec(mxUtils.createXmlDocument());
		var node = enc.encode(graph.getModel());
		textarea.value = mxUtils.getPrettyXml(node);
		showModalWindow('XML', textarea, 410, 440);
	});
	toolbar.appendChild(exportbutton);
}

//找到parent节点的所有出边，parent的样式为style
/*
 function getChildrenEdges(parent) {
 var style = parent.style;
 var edges = parent.edges;
 var childrenEdges = new Array();
 for(var j = 0; j < edges.length; j++) {
 if(edges[j].source.style == style) {
 childrenEdges.push(edges[j]);
 }
 }
 return childrenEdges;
 }
 */





function getParentCell(cell) {
	var style = cell.style;
	var edges = cell.edges;
	for(var i = 0; i < edges.length; i++) {
		if(edges[i].target.style == style) {
			return edges[i].source;
		}
	}
}




