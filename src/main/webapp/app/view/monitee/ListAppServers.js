Ext.require(['Ext.ux.CheckColumn']);

Ext.define('PaaSMonitor.view.monitee.ListAppServers', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listAppServers',
	store : 'AppServers',
	selType : 'cellmodel',

	initComponent : function() {
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToEdit : 1
		});
		this.columns = [{
			xtype : 'checkcolumn',
			header : 'Monitee?',
			dataIndex : 'isMonitee'
		}, {
			header : 'Name',
			dataIndex : 'name'
		}, {
			header : 'Ip',
			dataIndex : 'ip'
		}, {
			header : 'Status',
			dataIndex : 'status'
		}, {
			xtype : 'actioncolumn',
			header : 'Delete',
			align : 'center',
			items : [{
				icon : 'images/delete.gif',
				tooltip : 'Delete this App Server',
				handler : function(grid, rowIndex, colIndex) {
					var record = grid.store.getAt(rowIndex);
					Ext.Msg.show({
						title : 'Delete this App Server?',
						msg : 'Are you sure to delete this app server?',
						buttons : Ext.Msg.YESNOCANCEL,
						fn : function(e) {
							if(e == "yes") {
								grid.getStore().remove(record);
								grid.getStore().sync();
							}
						},
						icon : Ext.window.MessageBox.QUESTION
					});
				}
			}]
		}];
		this.plugins = [cellEditing];

		this.callParent(arguments);
	}
});
