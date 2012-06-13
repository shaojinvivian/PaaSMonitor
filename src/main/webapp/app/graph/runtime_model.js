
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

function getChildren(parent) {
	var style = parent.style;
	var edges = parent.edges;
	var children = new Array();
	for(var j = 0; j < edges.length; j++) {
		if(edges[j].source.style == style) {
			children.push(edges[j].target);
		}
	}
	return children;
}

function getChildType(parent) {
	if(parent == 'Phym')
		return 'Vim';
	if(parent == 'Vim')
		return 'AppServer';
	if(parent == 'AppServer')
		return 'AppInstance';
}

function getParentCell(cell) {
	var style = cell.style;
	var edges = cell.edges;
	for(var i = 0; i < edges.length; i++) {
		if(edges[i].target.style == style) {
			return edges[i].source;
		}
	}
}

//将字符串s的首字母变成大写
function upperFirstLetter(str) {
	first = str.substring(0, 1).toUpperCase();
	rest = str.substring(1, str.length);
	return first + rest;
}

function lowerFirstLetter(str) {
	first = str.substring(0, 1).toLowerCase();
	rest = str.substring(1, str.length);
	return first + rest;
}



