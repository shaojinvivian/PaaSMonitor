Ext.define('PaaSMonitor.view.monitee.AddPhym', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.addPhym',

	title : 'Add a new Physical Machine',
	store : 'Phyms',

	initComponent : function() {
		this.items = [{
			xtype : 'form',	
			bodyPadding: 10,		
			items : [{
				xtype : 'textfield',
				name : 'ip',
				fieldLabel : 'IP'
			}, {
				xtype : 'textfield',
				name : 'username',
				fieldLabel : 'User Name'
			}, {
				xtype : 'textfield',
				name : 'password',
				fieldLabel : 'Password'
			}],
			
			buttons : [{
				text : 'Next',
				action : 'save'
			}, {
				text : 'Cancel',
				scope : this,
				handler : this.close
			}],			
			
		}];

		this.callParent(arguments);
	}
});
