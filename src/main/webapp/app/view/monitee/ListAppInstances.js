Ext.require(['Ext.ux.CheckColumn']);

Ext.define('PaaSMonitor.view.monitee.ListAppInstances', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listAppInstances',
	store : 'AppInstances',
	// selType : 'cellmodel',
	height : 400,
	width : '100%',
	
	initComponent : function() {
		var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {
			groupHeaderTpl : 'AppServer: {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
		});

		// var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
			// clicksToEdit : 1
		// });
		var store = this.store;
		this.columns = [{
			header : 'Name',
			dataIndex : 'name'
		}, {
			header : 'docBase',
			dataIndex : 'docBase',
			width : 300
		}, {
			header : 'Description',
			dataIndex : 'description',
			width : 200
		}];
		// this.plugins = [cellEditing];
		this.features = [groupingFeature];
		this.autoScroll = true;

		this.callParent(arguments);
	}
});
