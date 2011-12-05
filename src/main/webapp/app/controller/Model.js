Ext.define('PaaSMonitor.controller.Model', {
	extend : 'Ext.app.Controller',	
	views : ['model.Diagram', 'model.Definition'],
	models : ['MBeanAttribute'],
	

	init : function() {		
		this.control({			
			'modelDefinition' : {
				beforerender : this.draw
			},			
			'modelDiagram' : {
				beforeactivate : this.updateModel
			}		
		});
	},
	
	
	draw : function() {
		main(document.getElementById('graphContainer'),
			document.getElementById('outlineContainer'),
		 	document.getElementById('toolbarContainer'),
			document.getElementById('sidebarContainer'),
			document.getElementById('statusContainer'));
	},
		
	updateModel : function(panel){		
		var graph = panel.diagramGraph;
		var ajax = Ext.Ajax.request({
		url : 'model/generate',		
		method : 'get',
		success : function(response) {
			var req = mxUtils.load('modelDiagram.xml');
			var root = req.getDocumentElement();
			var dec = new mxCodec(root);
			graph.getModel().beginUpdate();
			dec.decode(root, graph.getModel());			
			graph.getModel().endUpdate();
		},
		failure : function(response) {
			alert("The model has been save successfully!");
		}
	});		
	}
	
});


		
	
		
		
		
		
		
		

	