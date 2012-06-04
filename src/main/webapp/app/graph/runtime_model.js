function generateModel(container, toolbar, modelData) {
	// Checks if browser is supported
	if(!mxClient.isBrowserSupported()) {
		// Displays an error message if the browser is
		// not supported.
		mxUtils.error('Browser is not supported!', 200, false);
	} else {
		// Creates the graph inside the given container
		var editor = new mxEditor();
		var graph = editor.graph;
		var model = graph.model;
		editor.setGraphContainer(container);

		constructToolbar(toolbar, graph);

		var layout = new mxHierarchicalLayout(graph);
		layout.intraCellSpacing = 50;

		//返回cell的value的name，作为cell的label
		graph.convertValueToString = function(cell) {
			if(cell.value != null && cell.value.httpPort != null) {
				return cell.value.httpPort + ':' + cell.value.status;
			}
			if(cell.value != null && cell.value.ip != null) {
				return cell.value.ip;
			}
			if(cell.value != null && cell.value.name != null) {
				return cell.value.name;
			}
			return mxGraph.prototype.convertValueToString.apply(this, arguments);
			// "supercall"
		};

		graph.isHtmlLabel = function(cell) {
			return !this.isSwimlane(cell) && !this.model.isEdge(cell);
		};

		graph.getLabel = function(cell) {
			if(this.isHtmlLabel(cell)) {
				if(cell.value != null && cell.value.httpPort != null) {
					var label = '';
					label += '<div>' + mxUtils.htmlEntities(cell.value.httpPort, false) + '</div>' + '<span style="color:red; font-style: italic">' + mxUtils.htmlEntities(cell.value.status, false) + '</span>';
					return label;
				}
			}

			return mxGraph.prototype.getLabel.apply(this, arguments);
			// "supercall"
		};

		graph.isCellEditable = function(cell) {
			return false;
		};

		graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt) {
			var cell = evt.getProperty('cell');
			if(cell.value.jmxPort != null) {
				var edges = cell.edges;
				var hasChildren;
				for(var i=0; i< edges.length; i++){
					if(edges[i].source.style == 'appServer'){
						hasChildren = true;
						break;
					}
				}				
				if(!hasChildren){
					var appInstances = cell.value.appInstances;
					graph.getModel().beginUpdate();
					try {
						for(var i = 0; i < appInstances.length; i++) {
							var appInstanceObject = new AppInstance(appInstances[i]);
							var appInstance = graph.insertVertex(parent, appInstanceObject.name, appInstanceObject, 0, 0, 48, 48, 'appInstance');
							var server_to_instance = graph.insertEdge(parent, null, '', cell, appInstance);
						}
						layout.execute(parent);
					} finally {// Updates the display
						graph.getModel().endUpdate();
					}
				}else{
					for(var i=0; i< edges.length; i++){
						var edge = edges[i];
						if(edge.source.style == 'appServer'){
								// var terminal = this.model.getTerminal(edge, true);
								var target = edge.target;
								this.model.remove(edge);
								this.model.remove(target);
							}
					}	
					
				}				
			}

		});

		// Adds rubberband selection
		//能够用画框的方式选中element
		new mxRubberband(graph);

		// Creates a layout algorithm to be used
		// with the graph

		// layout.fixRoots = true;

		var parent = graph.getDefaultParent();
		configureStylesheet(graph);

		// Load cells and layouts the graph
		graph.getModel().beginUpdate();
		try {
			parseModelData(graph, modelData, layout);
			layout.execute(parent);
		} finally {
			// Updates the display

			graph.getModel().endUpdate();
		}

		if(mxClient.IS_IE) {
			new mxDivResizer(container);
		}
	}
};

function parseModelData(graph, modelData, layout) {

	var parent = graph.getDefaultParent();

	for(var i = 0; i < modelData.length; i++) {
		var phymObject = new Phym(modelData[i]);
		var phym = graph.insertVertex(parent, phymObject.ip, phymObject, 0, 0, 48, 48, 'phym');
		var vims = modelData[i].vims;
		for(var j = 0; j < vims.length; j++) {
			var vimObject = new Vim(vims[j]);
			var vim = graph.insertVertex(parent, vimObject.ip, vimObject, 0, 0, 48, 48, 'vim');
			var phym_to_vim = graph.insertEdge(parent, null, '', phym, vim);
			var appServers = vims[j].appServers;
			for(var asi = 0; asi < appServers.length; asi++) {
				var appServerObject = new AppServer(appServers[asi]);
				var appserver = graph.insertVertex(parent, null, appServerObject, 0, 0, 48, 48, 'appServer');
				var vim_to_appserver = graph.insertEdge(parent, null, '', vim, appserver);
				/*
				 var appInstances = appServers[asi].appInstances;
				 for(var q = 0; q < appInstances.length; q++){
				 var appInstanceObject = new AppInstance(appInstances[q]);
				 var appInstance = graph.insertVertex(appserver, appInstanceObject.name, appInstanceObject, 0, 0, 48, 48, 'appInstance');
				 var server_to_instance = graph.insertEdge(parent, null, '', appserver, appInstance);
				 }
				 */
			}
		}
	}
}

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

function createMoniteeStyleObject(image) {
	style = new Object();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
	style[mxConstants.STYLE_VERTICAL_LABEL_POSITION] = mxConstants.ALIGN_BOTTOM;
	// style[mxConstants.STYLE_STROKECOLOR] = '#1B78C8';
	style[mxConstants.STYLE_IMAGE] = image;
	style[mxConstants.STYLE_IMAGE_WIDTH] = '48';
	style[mxConstants.STYLE_IMAGE_HEIGHT] = '48';

	style[mxConstants.STYLE_FONTCOLOR] = '#000000';
	style[mxConstants.STYLE_STROKEWIDTH] = '0';
	style[mxConstants.STYLE_STARTSIZE] = '20';
	style[mxConstants.STYLE_FONTSIZE] = '12';
	style[mxConstants.STYLE_FONTSTYLE] = 1;
	return style;
}

function configureStylesheet(graph) {
	var phymImage = 'images/icons48/phym.png';
	graph.getStylesheet().putCellStyle('phym', createMoniteeStyleObject(phymImage));
	vimStyle = createMoniteeStyleObject('images/icons48/bigvim.png');
	graph.getStylesheet().putCellStyle('vim', vimStyle);
	serviceStyle = createMoniteeStyleObject('images/icons48/service.png');
	graph.getStylesheet().putCellStyle('service', serviceStyle);
	appServerStyle = createMoniteeStyleObject('images/icons48/tomcatserver.png');
	graph.getStylesheet().putCellStyle('appServer', appServerStyle);
	appStyle = createMoniteeStyleObject('images/icons48/app.png');
	graph.getStylesheet().putCellStyle('app', appStyle);
	appInstanceStyle = createMoniteeStyleObject('images/icons48/appInstance.png');
	graph.getStylesheet().putCellStyle('appInstance', appInstanceStyle);
	paasUserStyle = createMoniteeStyleObject('images/icons48/paasUser.png');
	graph.getStylesheet().putCellStyle('paasUser', paasUserStyle);
	style = graph.stylesheet.getDefaultEdgeStyle();
	style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
	style[mxConstants.STYLE_STROKEWIDTH] = '2';
	style[mxConstants.STYLE_ROUNDED] = true;
	style[mxConstants.STYLE_ENDARROW] = mxConstants.NONE;

	// style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;

};
