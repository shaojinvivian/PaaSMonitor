Ext.define('PaaSMonitor.view.model.Definition', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.modelDefinition',
	title : 'Model Definition',
	layout : 'border',
	
	initComponent : function() {

		this.items = [{
			region : 'north',
			contentEl : 'toolbarContainer',
			autoHeight : true,
			border : false,
			margins : '0 0 5 0'
		}, {
			region : 'south',
			contentEl : 'statusContainer',
			split : true
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
		}];
		this.callParent(arguments);
	}
});
