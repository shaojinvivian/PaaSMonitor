Ext.require(['Ext.ux.CheckColumn']);

Ext.define('PaaSMonitor.view.monitee.ListPhyms', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.listPhyms',
	store : 'Phyms',
	selType : 'cellmodel',

	initComponent : function() {
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToEdit : 1
		});
		this.columns = [{
			xtype : 'checkcolumn',
			header : 'Monitee?',
			dataIndex : 'isMonitee'	
		},{
			header : 'Name',
			dataIndex : 'name'			
		}, {
			header : 'Ip',
			dataIndex : 'ip'			
		}, {
			header : 'Username',
			dataIndex : 'username',			
			field : {
				xtype : 'textfield',
				allowBlank : false
			}

		}, {
			header : 'Password',
			dataIndex : 'password',			
			field : {
				xtype : 'textfield',
				allowBlank : false
			}				
		}, {
			header : 'Delete',
			xtype : 'actioncolumn',		
			align: 'center',	
			items : [{
				icon : 'images/delete.gif',
				tooltip : 'Delete this phym',
				handler : function(grid, rowIndex, colIndex) {
					var record = grid.store.getAt(rowIndex);
					Ext.Msg.show({
						title : 'Delete this phym?',
						msg : 'Are you sure to delete this phym?',
						buttons : Ext.Msg.YESNOCANCEL,
						fn : function(e) {
							if(e == "yes") {
								record.destroy();
								grid.getStore().load();
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
