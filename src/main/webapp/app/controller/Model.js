Ext.define('PaaSMonitor.controller.Model', {
	extend : 'Ext.app.Controller',
	// stores : ['Phyms', 'Vims', 'MoniteeTree', 'AppServers', 'AppInstances'],
	// models : ['Phym', 'Vim', 'AppServer', 'AppInstance'],
	views : ['model.Diagram', 'model.Definition'],

	init : function() {
		this.control({			
			'modelDefinition' : {
				beforerender : this.draw
			}			
		});
	},
	
	
	draw : function() {
		main(document.getElementById('graphContainer'),
			document.getElementById('outlineContainer'),
		 	document.getElementById('toolbarContainer'),
			document.getElementById('sidebarContainer'),
			document.getElementById('statusContainer'));
	}
	
	
});


		
	
		
		
		
		
		
		

	