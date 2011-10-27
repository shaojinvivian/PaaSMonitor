Ext.define('PaaSMonitor.view.model.Diagram', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.modelDiagram',

	title : 'Model Diagram',
	contentEl : 'page',
	// store : 'AppInstances',

	initComponent : function() {
		// this.html = '<div id="graphContainer"></div>';	
		// var start = Ext.get('start-div');
		// this.add(start);
		
		this.callParent(arguments);
	}
});

