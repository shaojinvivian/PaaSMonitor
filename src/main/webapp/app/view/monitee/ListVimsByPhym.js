Ext.require(['Ext.ux.CheckColumn']);

// Ext.require(['Ext.selection.CellModel', 'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.state.*', 'Ext.form.*', 'Ext.ux.CheckColumn']);

Ext.define('PaaSMonitor.view.monitee.ListVimsByPhym', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listVimsByPhym',
	title : 'Please select the virtual machines you want to monitor:',
	store : 'Vims',
	selType: 'cellmodel',
	
   /* plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })
    ],*/
    

	initComponent : function() {		
		var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {
			groupHeaderTpl : 'Phym:  {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
		});


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
			header : 'Phym Name',
			dataIndex : 'phymName'
		}, {
			header : 'Power State',
			dataIndex : 'powerState'
		}, {
			xtype : 'checkcolumn',
			header : 'Monitee?',
			dataIndex : 'isMonitee'
		}];
		this.plugins = [cellEditing];
		this.features = [groupingFeature];
		
		this.callParent(arguments);
	}
});
