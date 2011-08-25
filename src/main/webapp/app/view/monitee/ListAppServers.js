Ext.Loader.setConfig({
	enabled : true
});
Ext.Loader.setPath('Ext.ux', 'lib');
Ext.require(['Ext.ux.CheckColumn']);

// Ext.require(['Ext.selection.CellModel', 'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.state.*', 'Ext.form.*', 'Ext.ux.CheckColumn']);

Ext.define('PaaSMonitor.view.monitee.ListAppServers', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listAppServers',
	// title : 'Please select the virtual machines you want to monitor:',
	store : 'AppServers',
	selType: 'cellmodel',
   /* plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })
    ],*/
    

	initComponent : function() {
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToEdit : 1
		});
		var store = this.store;
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
