Ext.require(['Ext.ux.CheckColumn', 'Ext.selection.CheckboxModel']);

// Ext.require(['Ext.selection.CellModel', 'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.state.*', 'Ext.form.*', 'Ext.ux.CheckColumn']);

Ext.define('PaaSMonitor.view.monitee.ListVimsByPhym', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listVimsByPhym',
	title : 'Please select the virtual machines you want to monitor:',
	store : 'Vims',
	// selType: 'cellmodel',
	
	initComponent : function() {		
		var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {
			groupHeaderTpl : 'Phym:  {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
		});

		// var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
			// clicksToEdit : 1
		// });
		
		var selModel = Ext.create('Ext.selection.CheckboxModel');
		
		this.columns = [{
			header : 'Name',
			dataIndex : 'name'
		}, {
			header : 'Ip',
			dataIndex : 'ip'		
		}, {
			header : 'Power State',
			dataIndex : 'powerState'
		}];
		// this.plugins = [cellEditing];
		this.features = [groupingFeature];		
		this.selModel = selModel;
		this.callParent(arguments);
	}
});
