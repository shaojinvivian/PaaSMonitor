// Program starts here. Creates a sample graph in the
// DOM node with the specified ID. This function is invoked
// from the onLoad event handler of the document (see below).

var mappingWin;

function main(container, outline, toolbar, sidebar, status) {
	// Checks if the browser is supported
	if(!mxClient.isBrowserSupported()) {
		// Displays an error message if the browser is not supported.
		mxUtils.error('Browser is not supported!', 200, false);
	} else {
		// Specifies shadow opacity, color and offset
		mxConstants.SHADOW_OPACITY = 0.5;
		mxConstants.SHADOWCOLOR = '#C0C0C0';
		mxConstants.SHADOW_OFFSET_X = 5;
		mxConstants.SHADOW_OFFSET_Y = 6;
		mxConstants.SVG_SHADOWTRANSFORM = 'translate(' + mxConstants.SHADOW_OFFSET_X + ' ' + mxConstants.SHADOW_OFFSET_Y + ')';

		// Table icon dimensions and position
		mxSwimlane.prototype.imageSize = 20;
		mxSwimlane.prototype.imageDx = 16;
		mxSwimlane.prototype.imageDy = 4;

		// Implements white content area for swimlane in SVG
		mxSwimlaneCreateSvg = mxSwimlane.prototype.createSvg;
		mxSwimlane.prototype.createSvg = function() {
			var node = mxSwimlaneCreateSvg.apply(this, arguments);

			this.content.setAttribute('fill', '#FFFFFF');

			return node;
		};
		// Implements full-height shadow for SVG
		mxSwimlaneReconfigure = mxSwimlane.prototype.reconfigure;
		mxSwimlane.prototype.reconfigure = function(node) {
			mxSwimlaneReconfigure.apply(this, arguments);

			if(this.dialect == mxConstants.DIALECT_SVG && this.shadowNode != null) {
				this.shadowNode.setAttribute('height', this.bounds.height);
			}
		};
		// Implements table icon position and full-height shadow for SVG repaints
		mxSwimlaneRedrawSvg = mxSwimlane.prototype.redrawSvg;
		mxSwimlane.prototype.redrawSvg = function() {
			mxSwimlaneRedrawSvg.apply(this, arguments);

			// Full-height shadow
			if(this.dialect == mxConstants.DIALECT_SVG && this.shadowNode != null) {
				this.shadowNode.setAttribute('height', this.bounds.height);
			}

			// Positions table icon
			if(this.imageNode != null) {
				this.imageNode.setAttribute('x', this.bounds.x + this.imageDx * this.scale);
				this.imageNode.setAttribute('y', this.bounds.y + this.imageDy * this.scale);
			}
		};
		// Implements table icon position for swimlane in VML
		mxSwimlaneRedrawVml = mxSwimlane.prototype.redrawVml;
		mxSwimlane.prototype.redrawVml = function() {
			mxSwimlaneRedrawVml.apply(this, arguments);

			// Positions table icon
			if(this.imageNode != null) {
				this.imageNode.style.left = Math.floor(this.imageDx * this.scale) + 'px';
				this.imageNode.style.top = Math.floor(this.imageDy * this.scale) + 'px';
			}
		};
		// Replaces the built-in shadow with a custom shadow and adds
		// white content area for swimlane in VML
		mxSwimlaneCreateVml = mxSwimlane.prototype.createVml;
		mxSwimlane.prototype.createVml = function() {
			this.isShadow = false;
			var node = mxSwimlaneCreateVml.apply(this, arguments);
			this.shadowNode = document.createElement('v:rect');

			// Position and size of shadow are static
			this.shadowNode.style.left = mxConstants.SHADOW_OFFSET_X + 'px';
			this.shadowNode.style.top = mxConstants.SHADOW_OFFSET_Y + 'px';
			this.shadowNode.style.width = '100%'
			this.shadowNode.style.height = '100%'

			// Color for shadow fill
			var fillNode = document.createElement('v:fill');
			this.updateVmlFill(fillNode, mxConstants.SHADOWCOLOR, null, null, mxConstants.SHADOW_OPACITY * 100);
			this.shadowNode.appendChild(fillNode);

			// Color and weight of shadow stroke
			this.shadowNode.setAttribute('strokecolor', mxConstants.SHADOWCOLOR);
			this.shadowNode.setAttribute('strokeweight', (this.strokewidth * this.scale) + 'px');

			// Opacity of stroke
			var strokeNode = document.createElement('v:stroke');
			strokeNode.setAttribute('opacity', (mxConstants.SHADOW_OPACITY * 100) + '%');
			this.shadowNode.appendChild(strokeNode);

			node.insertBefore(this.shadowNode, node.firstChild);

			// White content area of swimlane
			this.content.setAttribute('fillcolor', 'white');
			this.content.setAttribute('filled', 'true');

			// Sets opacity on content area fill
			if(this.opacity != null) {
				var contentFillNode = document.createElement('v:fill');
				contentFillNode.setAttribute('opacity', this.opacity + '%');
				this.content.appendChild(contentFillNode);
			}

			return node;
		};
		// Defines an icon for creating new connections in the connection handler.
		// This will automatically disable the highlighting of the source vertex.
		mxConnectionHandler.prototype.connectImage = new mxImage('images/connector.gif', 16, 16);

		// Prefetches all images that appear in colums
		// to avoid problems with the auto-layout
		var keyImage = new Image();
		keyImage.src = "images/key.png";

		var plusImage = new Image();
		plusImage.src = "images/plus.png";

		var checkImage = new Image();
		checkImage.src = "images/check.png";

		// Workaround for Internet Explorer ignoring certain CSS directives
		if(mxClient.IS_IE) {
			new mxDivResizer(container);
			new mxDivResizer(outline);
			new mxDivResizer(toolbar);
			new mxDivResizer(sidebar);
			new mxDivResizer(status);
		}

		// Creates the graph inside the given container. The
		// editor is used to create certain functionality for the
		// graph, such as the rubberband selection, but most parts
		// of the UI are custom in this example.
		var editor = new mxEditor();
		var graph = editor.graph;
		var model = graph.model;

		// Disables some global features
		graph.setConnectable(true);
		graph.setCellsDisconnectable(false);
		graph.setCellsCloneable(false);
		graph.swimlaneNesting = false;
		graph.dropEnabled = true;

		// Does not allow dangling edges
		graph.setAllowDanglingEdges(false);

		// Forces use of default edge in mxConnectionHandler
		graph.connectionHandler.factoryMethod = null;

		// Only tables are resizable
		graph.isCellResizable = function(cell) {
			return this.isSwimlane(cell);
		};
		// Only tables are movable
		graph.isCellMovable = function(cell) {
			return this.isSwimlane(cell);
		};
		// Sets the graph container and configures the editor
		editor.setGraphContainer(container);
		var config = mxUtils.load('config/keyhandler-minimal.xml').getDocumentElement();
		editor.configure(config);

		// Configures the automatic layout for the table columns
		editor.layoutSwimlanes = true;
		editor.createSwimlaneLayout = function() {
			var layout = new mxStackLayout(this.graph, false);
			layout.fill = true;
			layout.resizeParent = true;

			// Overrides the function to always return true
			layout.isVertexMovable = function(cell) {
				return true;
			};
			return layout;
		};
		// Text label changes will go into the name field of the user object
		graph.model.valueForCellChanged = function(cell, value) {
			if(value.name != null) {
				return mxGraphModel.prototype.valueForCellChanged.apply(this, arguments);
			} else {
				var old = cell.value.name;
				cell.value.name = value;
				return old;
			}
		};
		// Columns are dynamically created HTML labels
		graph.isHtmlLabel = function(cell) {
			return !this.isSwimlane(cell) && !this.model.isEdge(cell);
		};
		// Edges are not editable
		graph.isCellEditable = function(cell) {
			return !this.model.isEdge(cell);
		};
		// Returns the name field of the user object for the label
		graph.convertValueToString = function(cell) {
			if(cell.value != null && cell.value.name != null) {
				return cell.value.name;
			}

			return mxGraph.prototype.convertValueToString.apply(this, arguments);
			// "supercall"
		};
		// Returns the type as the tooltip for column cells
		graph.getTooltip = function(state) {
			if(this.isHtmlLabel(state.cell)) {
				return 'Type: ' + state.cell.value.type;
			} else if(this.model.isEdge(state.cell)) {
				var source = this.model.getTerminal(state.cell, true);
				var parent = this.model.getParent(source);

				return parent.value.name + '.' + source.value.name;
			}

			return mxGraph.prototype.getTooltip.apply(this, arguments);
			// "supercall"
		};
		// Creates a dynamic HTML label for column fields
		graph.getLabel = function(cell) {
			if(this.isHtmlLabel(cell)) {
				var label = '';
				if(cell.value.category == 'Config') {
					label += '<img title="Config" src="images/icons48/settings.png" width="16" height="16" align="top">&nbsp;';
				}
				if(cell.value.category == 'Monitor') {
					label += '<img title="View" src="images/icons48/view.png" width="16" height="16" align="top">&nbsp;';
				}
				if(cell.value.category == 'Control') {
					label += '<img title="View" src="images/icons48/edit.png" width="16" height="16" align="top">&nbsp;';
				}

				return label + mxUtils.htmlEntities(cell.value.name, false) + '&nbsp;<span style="color:#B0B0B0; font-style: italic">' + mxUtils.htmlEntities(cell.value.type, false) + '</span>';
			}

			return mxGraph.prototype.getLabel.apply(this, arguments);
			// "supercall"
		};
		// Removes the source vertex if edges are removed
		graph.addListener(mxEvent.REMOVE_CELLS, function(sender, evt) {
			var cells = evt.getProperty('cells');

			for(var i = 0; i < cells.length; i++) {
				var cell = cells[i];

				if(this.model.isEdge(cell)) {
					var terminal = this.model.getTerminal(cell, true);
					var parent = this.model.getParent(terminal);
					this.model.remove(terminal);
				}
			}
		});
		// Disables drag-and-drop into non-swimlanes.
		graph.isValidDropTarget = function(cell, cells, evt) {
			return this.isSwimlane(cell);
		};
		// Installs a popupmenu handler using local function (see below).
		graph.panningHandler.factoryMethod = function(menu, cell, evt) {
			createPopupMenu(editor, graph, menu, cell, evt);
		};
		// Adds all required styles to the graph (see below)
		configureStylesheet(graph);
				
		
		// Adds sidebar icon for each object
		var attributeObject = new Attribute('ATTRIBUTENAME');
		var attribute = new mxCell(attributeObject, new mxGeometry(0, 0, 0, 26));
		attribute.setVertex(true);
		attribute.setConnectable(false);
		
		var phymObject = new Phym('Phym');
		var phym = new mxCell(phymObject, new mxGeometry(0, 0, 200, 28), 'phym');
		phym.setVertex(true);
		addSidebarIcon(graph, sidebar, phym, 'images/icons48/phym.png');
		addConfigs(phymObject, phym, attribute);
		
		var vimObject = new Vim('Vim');
		var vim = new mxCell(vimObject, new mxGeometry(0, 0, 200, 28), 'vim');
		vim.setVertex(true);
		addSidebarIcon(graph, sidebar, vim, 'images/icons48/vim.png');
		addConfigs(vimObject, vim, attribute);
		
		var appServerObject = new AppServer('AppServer');
		var appServer = new mxCell(appServerObject, new mxGeometry(0, 0, 200, 28), 'appServer');
		appServer.setVertex(true);
		addSidebarIcon(graph, sidebar, appServer, 'images/icons48/appServer.png');
		addConfigs(appServerObject, appServer, attribute);
		
		var appObject = new App('App');
		var app = new mxCell(appObject, new mxGeometry(0, 0, 200, 28), 'app');
		app.setVertex(true);
		addSidebarIcon(graph, sidebar, app, 'images/icons48/app.png');
		addConfigs(appObject, app, attribute);
		
		var appInstanceObject = new AppInstance('AppInstance');
		var appInstance = new mxCell(appInstanceObject, new mxGeometry(0, 0, 200, 28), 'appInstance');
		appInstance.setVertex(true);
		addSidebarIcon(graph, sidebar, appInstance, 'images/icons48/appInstance.png');
		addConfigs(appInstanceObject, appInstance, attribute);
		
		sidebar.appendChild(createEdgeTemplate(graph, 'libStraight', 'images/straight.gif', 'edgeStyle=none', 100, 100));

		sidebar.appendChild(createEdgeTemplate(graph, 'libEntityRel', 'images/entity.gif', 'edgeStyle=entityRelationEdgeStyle', 100, 100));

	

		/*
		// Adds child columns for new connections between tables
		graph.addEdge = function(edge, parent, source, target, index)
		{
		// Finds the primary key child of the target table
		var primaryKey = null;
		var childCount = this.model.getChildCount(target);

		for (var i=0; i < childCount; i++)
		{
		var child = this.model.getChildAt(target, i);

		if (child.value.primaryKey)
		{
		primaryKey = child;
		break;
		}
		}

		if (primaryKey == null)
		{
		mxUtils.alert('Target table must have a primary key');
		return;
		}

		this.model.beginUpdate();
		try
		{
		var col1 = this.model.cloneCell(column);
		col1.value.name = primaryKey.value.name;
		col1.value.type = primaryKey.value.type;

		this.addCell(col1, source);
		source = col1;
		target = primaryKey;

		return mxGraph.prototype.addEdge.apply(this, arguments); // "supercall"
		}
		finally
		{
		this.model.endUpdate();
		}

		return null;
		};
		*/
		// Creates a new DIV that is used as a toolbar and adds
		// toolbar buttons.
		var spacer = document.createElement('div');
		spacer.style.display = 'inline';
		spacer.style.padding = '8px';

		addToolbarButton(editor, toolbar, 'properties', 'Properties', 'images/properties.gif');

		// Defines a new export action
		editor.addAction('properties', function(editor, cell) {
			if(cell == null) {
				cell = graph.getSelectionCell();
			}

			if(graph.isHtmlLabel(cell)) {
				showProperties(graph, cell);
			}
		});

		editor.addAction('add', function(editor, cell) {
			/*
			if (cell == null)
			{
			cell = graph.getSelectionCell();
			}
			*/
			// if (graph.isHtmlLabel(cell))
			// {
			addAttribute(graph, cell);
			// }
		});

		editor.addAction('mapping', function(editor, cell) {
			/*
			if (cell == null)
			{
			cell = graph.getSelectionCell();
			}
			*/
			// if (graph.isHtmlLabel(cell))
			// {
			mapping();
			// }
		});
		addToolbarButton(editor, toolbar, 'delete', 'Delete', 'images/delete2.png');

		toolbar.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, toolbar, 'undo', '', 'images/undo.png');
		addToolbarButton(editor, toolbar, 'redo', '', 'images/redo.png');

		toolbar.appendChild(spacer.cloneNode(true));

		// addToolbarButton(editor, toolbar, 'show', 'Show', 'images/camera.png');
		// addToolbarButton(editor, toolbar, 'print', 'Print', 'images/printer.png');

		// toolbar.appendChild(spacer.cloneNode(true));

		// Defines export XML action
		editor.addAction('export', function(editor, cell) {
			var textarea = document.createElement('textarea');
			textarea.style.width = '400px';
			textarea.style.height = '400px';
			var enc = new mxCodec(mxUtils.createXmlDocument());
			var node = enc.encode(editor.graph.getModel());
			textarea.value = mxUtils.getPrettyXml(node);
			showModalWindow('XML', textarea, 410, 440);
		});
		addToolbarButton(editor, toolbar, 'export', 'Export XML', 'images/export1.png');

		// Adds toolbar buttons into the status bar at the bottom
		// of the window.
		addToolbarButton(editor, status, 'collapseAll', 'Collapse All', 'images/navigate_minus.png', true);
		addToolbarButton(editor, status, 'expandAll', 'Expand All', 'images/navigate_plus.png', true);

		status.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, status, 'zoomIn', '', 'images/zoom_in.png', true);
		addToolbarButton(editor, status, 'zoomOut', '', 'images/zoom_out.png', true);
		addToolbarButton(editor, status, 'actualSize', '', 'images/view_1_1.png', true);
		addToolbarButton(editor, status, 'fit', '', 'images/fit_to_size.png', true);

		// Creates the outline (navigator, overview) for moving
		// around the graph in the top, right corner of the window.
		var outln = new mxOutline(graph, outline);

		// Fades-out the splash screen after the UI has been loaded.
		var splash = document.getElementById('splash');
		if(splash != null) {
			try {
				mxEvent.release(splash);
				mxEffects.fadeOut(splash, 100, true);
			} catch (e) {

				// mxUtils is not available (library not loaded)
				splash.parentNode.removeChild(splash);
			}
		}
	}
}

function addToolbarButton(editor, toolbar, action, label, image, isTransparent) {
	var button = document.createElement('button');
	button.style.fontSize = '10';
	if(image != null) {
		var img = document.createElement('img');
		img.setAttribute('src', image);
		img.style.width = '16px';
		img.style.height = '16px';
		img.style.verticalAlign = 'middle';
		img.style.marginRight = '2px';
		button.appendChild(img);
	}
	if(isTransparent) {
		button.style.background = 'transparent';
		button.style.color = '#FFFFFF';
		button.style.border = 'none';
	}
	mxEvent.addListener(button, 'click', function(evt) {
		editor.execute(action);
	});
	mxUtils.write(button, label);
	toolbar.appendChild(button);
};

function showModalWindow(title, content, width, height) {
	var background = document.createElement('div');
	background.style.position = 'absolute';
	background.style.left = '0px';
	background.style.top = '0px';
	background.style.right = '0px';
	background.style.bottom = '0px';
	background.style.background = 'black';
	mxUtils.setOpacity(background, 50);
	document.body.appendChild(background);

	if(mxClient.IS_IE) {
		new mxDivResizer(background);
	}

	var x = Math.max(0, document.body.scrollWidth / 2 - width / 2);
	var y = Math.max(10, (document.body.scrollHeight || document.documentElement.scrollHeight) / 2 - height * 2 / 3);
	var wnd = new mxWindow(title, content, x, y, width, height, false, true);
	wnd.setClosable(true);

	// Fades the background out after after the window has been closed
	wnd.addListener(mxEvent.DESTROY, function(evt) {
		mxEffects.fadeOut(background, 50, true, 10, 30, true);
	});

	wnd.setVisible(true);

	return wnd;
};

function addSidebarIcon(graph, sidebar, prototype, image) {
	// Function that is executed when the image is dropped on
	// the graph. The cell argument points to the cell under
	// the mousepointer if there is one.
	var funct = function(graph, evt, cell) {
		graph.stopEditing(false);

		var pt = graph.getPointForEvent(evt);

		var parent = graph.getDefaultParent();
		var model = graph.getModel();

		var isTable = graph.isSwimlane(prototype);
		var name = null;
		/*

		 if (!isTable)
		 {
		 var offset = mxUtils.getOffset(graph.container);

		 parent = graph.getSwimlaneAt(evt.clientX-offset.x, evt.clientY-offset.y);
		 var pstate = graph.getView().getState(parent);

		 if (parent == null ||
		 pstate == null)
		 {
		 mxUtils.alert('Drop target must be a table');
		 return;
		 }

		 pt.x -= pstate.x;
		 pt.y -= pstate.y;

		 var columnCount = graph.model.getChildCount(parent)+1;
		 name = mxUtils.prompt('Enter name for new column', 'COLUMN'+columnCount);
		 }
		 else*/
		{
			/*
			 var tableCount = 0;
			 var childCount = graph.model.getChildCount(parent);

			 for (var i=0; i<childCount; i++)
			 {
			 if (!graph.model.isEdge(graph.model.getChildAt(parent, i)))
			 {
			 tableCount++;
			 }
			 }
			 */

			graph.model.getChildCount(parent) + 1;
		}

		var v1 = model.cloneCell(prototype);
		model.beginUpdate();
		try {
			// v1.value.name = name;
			value = v1.value;
			v1.geometry.x = pt.x;
			v1.geometry.y = pt.y;

			graph.addCell(v1, parent);

			// if (isTable)
			// {
			v1.geometry.alternateBounds = new mxRectangle(0, 0, v1.geometry.width, v1.geometry.height);
			// v1.children[0].value.value = ip;
			// v1.children[1].value.value = jmxPort;

			// }
		} finally {
			model.endUpdate();
		}

		graph.setSelectionCell(v1);

	}
	
	
	
	// Creates the image which is used as the sidebar icon (drag source)
	var img = document.createElement('img');
	img.setAttribute('src', image);
	img.style.width = '48px';
	img.style.height = '48px';
	img.style.margin = '5px';
	img.title = 'Drag this to the diagram to create a new vertex';
	sidebar.appendChild(img);

	// Creates the image which is used as the drag icon (preview)
	var dragImage = img.cloneNode(true);
	var ds = mxUtils.makeDraggable(img, graph, funct, dragImage);

	// Adds highlight of target tables for columns
	ds.highlightDropTargets = true;
	ds.getDropTarget = function(graph, x, y) {
		if(graph.isSwimlane(prototype)) {
			return null;
		} else {
			var cell = graph.getCellAt(x, y);

			if(graph.isSwimlane(cell)) {
				return cell;
			} else {
				var parent = graph.getModel().getParent(cell);

				if(graph.isSwimlane(parent)) {
					return parent;
				}
			}
		}
	};
};


function createMoniteeStyleObject(image){
	style = new Object();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_SWIMLANE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
	style[mxConstants.STYLE_GRADIENTCOLOR] = '#41B9F5';
	style[mxConstants.STYLE_FILLCOLOR] = '#8CCDF5';
	style[mxConstants.STYLE_STROKECOLOR] = '#1B78C8';
	style[mxConstants.STYLE_FONTCOLOR] = '#000000';
	style[mxConstants.STYLE_STROKEWIDTH] = '2';
	style[mxConstants.STYLE_STARTSIZE] = '28';
	style[mxConstants.STYLE_VERTICAL_ALIGN] = 'middle';
	style[mxConstants.STYLE_FONTSIZE] = '12';
	style[mxConstants.STYLE_FONTSTYLE] = 1;
	style[mxConstants.STYLE_IMAGE] = image;
	// Looks better without opacity if shadow is enabled
	//style[mxConstants.STYLE_OPACITY] = '80';
	style[mxConstants.STYLE_SHADOW] = 1;
	return style;
}



function configureStylesheet(graph) {
	var style = new Object();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
	style[mxConstants.STYLE_FONTCOLOR] = '#000000';
	style[mxConstants.STYLE_FONTSIZE] = '11';
	style[mxConstants.STYLE_FONTSTYLE] = 0;
	style[mxConstants.STYLE_SPACING_LEFT] = '4';
	style[mxConstants.STYLE_IMAGE_WIDTH] = '48';
	style[mxConstants.STYLE_IMAGE_HEIGHT] = '48';
	graph.getStylesheet().putDefaultVertexStyle(style);
	
	
	phymStyle = createMoniteeStyleObject('images/icons48/phym.png');
	graph.getStylesheet().putCellStyle('phym', phymStyle);
	
	vimStyle = createMoniteeStyleObject('images/icons48/vim.png');
	graph.getStylesheet().putCellStyle('vim', vimStyle);
	
	appServerStyle = createMoniteeStyleObject('images/icons48/appServer.png');
	graph.getStylesheet().putCellStyle('appServer', appServerStyle);
	
	appStyle = createMoniteeStyleObject('images/icons48/app.png');
	graph.getStylesheet().putCellStyle('app', appStyle);	
	
	appInstanceStyle = createMoniteeStyleObject('images/icons48/appInstance.png');
	graph.getStylesheet().putCellStyle('appInstance', appInstanceStyle);

	/*
	 style = graph.stylesheet.getDefaultEdgeStyle();
	 style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
	 style[mxConstants.STYLE_STROKEWIDTH] = '2';
	 style[mxConstants.STYLE_ROUNDED] = true;
	 style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
	 */
};

// Function to create the entries in the popupmenu
function createPopupMenu(editor, graph, menu, cell, evt) {
	if(cell != null) {
		if(graph.isHtmlLabel(cell)) {
			menu.addItem('Properties', 'images/properties.gif', function() {
				editor.execute('properties', cell);
			});
			if(cell.value.category == 'Monitor') {
				menu.addItem('Mapping', 'images/properties.gif', function() {
					editor.execute('mapping', cell);
				});
			}

			menu.addSeparator();

		} else {
			menu.addItem('Add Attribute', 'images/plus.png', function() {
				editor.execute('add', cell);
			});

			menu.addSeparator();
		}

		menu.addItem('Delete', 'images/delete2.png', function() {
			editor.execute('delete', cell);
		});

		menu.addSeparator();
	}

	menu.addItem('Undo', 'images/undo.png', function() {
		editor.execute('undo', cell);
	});

	menu.addItem('Redo', 'images/redo.png', function() {
		editor.execute('redo', cell);
	});
};

function addAttribute(graph, cell) {
	// Creates a form for the user object inside
	// the cell
	var form = new mxForm('addAttribute');
	// Adds a field for the columnname
	var nameField = form.addText('Name', '');
	var typeField = form.addCombo('Type', false, 1);
	form.addOption(typeField, 'String', 'String', false);
	form.addOption(typeField, 'int', 'int', false);

	var categoryField = form.addCombo('Category', false, 1);
	form.addOption(categoryField, 'Config', 'Config', false);
	form.addOption(categoryField, 'Monitor', 'Monitor', false);
	form.addOption(categoryField, 'Control', 'Control', false);

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var attribute = cell.children[0].clone();
		attribute.value.name = nameField.value;
		attribute.value.type = typeField.value;
		attribute.value.category = categoryField.value;
		// attribute.value.value = valueField.value;

		graph.model.beginUpdate();
		try {
			graph.addCell(attribute, cell);
		} finally {
			graph.model.endUpdate();
		}
		wnd.destroy();
	}
	// Defines the function to be executed when the
	// Cancel button is pressed in the dialog
	var cancelFunction = function() {
		wnd.destroy();
	}
	form.addButtons(okFunction, cancelFunction);
	wnd = showModalWindow('Add an attribute', form.table, 240, 240);
};

function showProperties(graph, cell) {
	// Creates a form for the user object inside
	// the cell
	var form = new mxForm('properties');

	// Adds a field for the columnname
	var nameField = form.addText('Name', cell.value.name);
	var typeField = form.addText('Type', cell.value.type);

	var primaryKeyField = form.addCheckbox('Primary Key', cell.value.primaryKey);
	var autoIncrementField = form.addCheckbox('Auto Increment', cell.value.autoIncrement);
	var notNullField = form.addCheckbox('Not Null', cell.value.notNull);
	var uniqueField = form.addCheckbox('Unique', cell.value.unique);

	var defaultField = form.addText('Default', cell.value.defaultValue || '');
	var useDefaultField = form.addCheckbox('Use Default', (cell.value.defaultValue != null));

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.value.clone();

		clone.name = nameField.value;
		clone.type = typeField.value;

		if(useDefaultField.checked) {
			clone.defaultValue = defaultField.value;
		} else {
			clone.defaultValue = null;
		}

		clone.primaryKey = primaryKeyField.checked;
		clone.autoIncrement = autoIncrementField.checked;
		clone.notNull = notNullField.checked;
		clone.unique = uniqueField.checked;

		graph.model.setValue(cell, clone);

		wnd.destroy();
	}
	// Defines the function to be executed when the
	// Cancel button is pressed in the dialog
	var cancelFunction = function() {
		wnd.destroy();
	}
	form.addButtons(okFunction, cancelFunction);

	var parent = graph.model.getParent(cell);
	var name = parent.value.name + '.' + cell.value.name;
	wnd = showModalWindow(name, form.table, 240, 240);
};

function createEdgeTemplate(graph, name, icon, style, width, height, value) {
	var cells = [new mxCell((value != null) ? value : '', new mxGeometry(0, 0, width, height), style)];
	cells[0].geometry.setTerminalPoint(new mxPoint(0, height), true);
	cells[0].geometry.setTerminalPoint(new mxPoint(width, 0), false);
	cells[0].edge = true;

	var funct = function(graph, evt, target) {
		cells = graph.getImportableCells(cells);

		if(cells.length > 0) {
			var validDropTarget = (target != null) ? graph.isValidDropTarget(target, cells, evt) : false;
			var select = null;

			if(target != null && !validDropTarget) {
				target = null;
			}

			var pt = graph.getPointForEvent(evt);
			var scale = graph.view.scale;

			pt.x -= graph.snap(width / 2);
			pt.y -= graph.snap(height / 2);
			select = graph.importCells(cells, pt.x, pt.y, target);

			graph.scrollCellToVisible(select[0]);
			graph.setSelectionCells(select);
		}
	};
	var node = createImg(name, icon);

	// Installs a click handler to set the edge template
	mxEvent.addListener(node, 'mousedown', function(evt) {
		edgeTemplate = cells[0];
	});
	// Creates the element that is being shown while the drag is in progress
	var dragPreview = document.createElement('div');
	dragPreview.style.border = 'dashed black 1px';
	dragPreview.style.width = width + 'px';
	dragPreview.style.height = height + 'px';

	mxUtils.makeDraggable(node, graph, funct, dragPreview, -width / 2, -height / 2, graph.autoscroll, true, false);

	return node;
};

function createImg(name, icon) {
	var node = mxUtils.createImage(icon);
	node.setAttribute('title', mxResources.get(name) || name);

	if(node.nodeName == 'IMG') {
		node.setAttribute('alt', mxResources.get(name) || name);

		if(mxClient.IS_TOUCH) {
			node.setAttribute('width', '36');
			node.setAttribute('height', '36');
		} else {
			node.setAttribute('width', '26');
			node.setAttribute('height', '26');
		}

		node.setAttribute('hspace', '5');
		node.setAttribute('vspace', '5');
	} else {
		node.style.width = '26px';
		node.style.height = '26px';
		node.style.margin = '5px';
		node.style.display = 'inline';
	}

	return node;
};

function Attribute(name) {
	this.name = name;
}

Attribute.prototype.type = 'String';
Attribute.prototype.category = '';
Attribute.prototype.clone = function() {
	return mxUtils.clone(this);
};
function Monitee(name) {
	this.name = name;
}

Monitee.prototype.clone = function() {
	return mxUtils.clone(this);
}
function AppServer(name) {
	this.name = name;
}

AppServer.prototype.ip = null;
AppServer.prototype.jmxPort = null;

AppServer.prototype.clone = function() {
	return mxUtils.clone(this);
}
function Vim(name) {
	this.name = name;
}

Vim.prototype.ip = null;

Vim.prototype.clone = function() {
	return mxUtils.clone(this);
}
function Phym(name) {
	this.name = name;
}

Phym.prototype.ip = null;

Phym.prototype.clone = function() {
	return mxUtils.clone(this);
}

function App(name) {
	this.name = name;
}

App.prototype.type = null;

App.prototype.clone = function() {
	return mxUtils.clone(this);
}


function AppInstance(name) {
	this.name = name;
}

AppInstance.prototype.type = null;

AppInstance.prototype.clone = function() {
	return mxUtils.clone(this);
}


function addConfigs(object, cell, config) {
	for(attri in object) {
		if(attri != 'name' && attri != 'clone') {
			var temp = config.clone();
			temp.value.name = attri;
			temp.value.category = 'Config';
			cell.insert(temp);
		}
	}
}

function mapping() {
	var tomcat6Tree = Ext.create('Ext.tree.Panel', {
		id : 'tomcat6Tree',
		title : 'Tomcat 6',
		height : 300,
		width : 200,
		rootVisible : false,
		autoScroll : true,
		collapsible : true,
		store : Ext.create('Ext.data.TreeStore', {
			proxy : {
				type : 'ajax',
				url : 'data/tomcat6-tree.txt'
			},
			root : {
				expanded : true
			}
		})
	});

	var tomcat7Tree = Ext.create('Ext.tree.Panel', {
		id : 'tomcat7Tree',
		title : 'Tomcat 7',
		height : 300,
		width : 200,
		rootVisible : false,
		autoScroll : true,
		collapsible : true,
		store : Ext.create('Ext.data.TreeStore', {
			proxy : {
				type : 'ajax',
				url : 'data/tomcat7-tree.txt'
			},
			root : {
				expanded : true
			}
		})
	});

	var jettyTree = Ext.create('Ext.tree.Panel', {
		id : 'jettyTree',
		title : 'Jetty',
		height : 300,
		width : 200,
		rootVisible : false,
		autoScroll : true,
		collapsible : true,
		store : Ext.create('Ext.data.TreeStore', {
			proxy : {
				type : 'ajax',
				url : 'data/jetty-tree.txt'
			},
			root : {
				expanded : true
			}
		})
	});
	if(mappingWin==null){
		mappingWin = Ext.create('widget.window', {
			title : 'Please choose the mapping',
			closable : true,
			closeAction : 'hide',
			//animateTarget: this,
			width : 600,
			height : 350,
			layout : 'border',
			bodyStyle : 'padding: 5px;',
			items : [{
				region : 'center',
				xtype : 'tabpanel',
				items : [tomcat6Tree, tomcat7Tree, jettyTree]
			}]
		});
	}	
	mappingWin.show();
}