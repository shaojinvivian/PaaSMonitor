Ext.require(['Ext.ux.CheckColumn']);

Ext.define('PaaSMonitor.view.monitee.ListAppServers', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listAppServers',	
	store : 'AppServers',
	selType: 'cellmodel',    

	initComponent : function() {
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToEdit : 1
		});		
		this.columns = [{
			header : 'Name',
			dataIndex : 'name'
		}, {
			header : 'Ip',
			dataIndex : 'ip'
		}, {
			xtype : 'checkcolumn',
			header : 'Monitee?',
			dataIndex : 'isMonitee'
		}];
		this.plugins = [cellEditing];
		
		this.callParent(arguments);
	}
});
