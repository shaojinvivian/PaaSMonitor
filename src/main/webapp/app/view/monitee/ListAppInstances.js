Ext.require(['Ext.ux.CheckColumn']);


Ext.define('PaaSMonitor.view.monitee.ListAppInstances', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listAppInstances',
	
	store : 'AppInstances',
	selType: 'cellmodel', 
    

	initComponent : function() {
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToEdit : 1
		});
		var store = this.store;
		this.columns = [{
			header : 'Name',
			dataIndex : 'name'
		}, {
			header : 'docBase',
			dataIndex : 'docBase'
		}, {
			header : 'Display Name',
			dataIndex : 'displayName'
		}, {
			xtype : 'checkcolumn',
			header : 'Monitee?',
			dataIndex : 'isMonitee'
		}];
		this.plugins = [cellEditing];
		
		this.callParent(arguments);
	}
});
