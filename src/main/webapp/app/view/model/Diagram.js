Ext.define('PaaSMonitor.view.model.Diagram', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.modelDiagram',
	title : 'Model Diagram',	
	layout : 'border',
	// contentEl : 'diagram',
	// store : 'AppInstances',

	initComponent : function() {
		this.items = [{
			region : 'center',
			title : 'Diagram',
			contentEl : 'diagramContainer',
			split : true,
			height : 100,
			minHeight : 100
		}, {
			region : 'east',
			title : 'Outline',
			contentEl : 'diagramOutlineContainer',
			split : true,
			width : 200
		}];
		
		var container = document.getElementById('diagramContainer');
		var outline = document.getElementById('diagramOutlineContainer');
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
		
		// Columns are dynamically created HTML labels
		graph.isHtmlLabel = function(cell) {
			return !this.isSwimlane(cell) && !this.model.isEdge(cell);
		};
		// Edges are not editable
		/*
		graph.isCellEditable = function(cell) {
		return !this.model.isEdge(cell);
		};
		*/
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

				var mapped = '';
				if(cell.value.mapped)
					mapped = '&nbsp;&nbsp;<img title="Mapped" src="images/check.png" width="16" height="16" align="top">'
				return label + mxUtils.htmlEntities(cell.value.name, false) + '&nbsp;<span style="color:#B0B0B0; font-style: italic">' + mxUtils.htmlEntities(cell.value.value, false) + '</span>' + mapped;
			}

			return mxGraph.prototype.getLabel.apply(this, arguments);
			// "supercall"
		};
		
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
		
		var outln = new mxOutline(graph, outline);
		this.diagramGraph = graph;		
		this.callParent(arguments);
		
	}
});

