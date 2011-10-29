Ext.define('PaaSMonitor.view.model.Diagram', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.modelDiagram',
	title : 'Model Diagram',
	layout : 'border',
	// contentEl : 'diagram',
	// store : 'AppInstances',

	initComponent : function() {
		
		this.items = [
		
			{
				region : 'north',
				contentEl : 'toolbarContainer',
				autoHeight : true,
				border : false,
				margins : '0 0 5 0'				
				 }, {
				 region : 'south',						
				 contentEl : 'statusContainer',
				 split : true,
				 height : 100,
				 minHeight : 100
				  }, {
				 region : 'west',
				 title : 'Monitee',				
				 contentEl : 'sidebarContainer',
				 width : 50	
				
				  }, {
				 region : 'center',
				 title : 'Diagram',				 
				 contentEl : 'graphContainer',
				 split : true,
				 height : 100,
				 minHeight : 100
				   }, {
				 region : 'east',
				 title : 'Outline',				 
				 contentEl : 'outlineContainer',
				 split : true,				
				 width : 200
				 
				 
			}			];
			
		this.callParent(arguments);
	}
});

