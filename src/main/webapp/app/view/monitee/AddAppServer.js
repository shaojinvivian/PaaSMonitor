Ext.define('PaaSMonitor.view.monitee.AddAppServer', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.addAppServer',

	title : 'Add a new Application Server',
	store : 'AppServers',

	initComponent : function() {
		var types = [['tomcat', 'Tomcat'], ['jetty', 'Jetty'], ['apache', 'Apache']];

		var store = new Ext.data.SimpleStore({
			fields : ["id", "name"],
			data : types
		});

		var combo = Ext.create('Ext.form.field.ComboBox', {
			name : 'type',
			fieldLabel : 'Type',
			editable : false,
			store : store,
			emptyText : 'Please choose the type',
			mode : 'local', //指定数据加载方式，如果直接从客户端加载则为local，如果从服务器断加载 则为remote.默认值为：remote
			// typeAhead: true,
			// triggerAction: 'all',
			valueField : 'id',
			displayField : 'name'
		});
		
		var jmxField = Ext.create('Ext.form.field.Text',{
					name : 'jmxPort',
					fieldLabel : 'JMX Port',
					hidden : true
				});

		var form = Ext.create('Ext.form.Panel', {
			xtype : 'form',
			bodyPadding : 10,
			items : [combo, {
				xtype : 'textfield',
				name : 'ip',
				fieldLabel : 'IP'
			}, {
				xtype : 'textfield',
				name : 'httpPort',
				fieldLabel : 'HTTP Port'
			}, jmxField],
			buttons : [{
				text : 'Next',
				action : 'save'
			}, {
				text : 'Cancel',
				scope : this,
				handler : this.close
			}],
		});
		
		
				

		combo.on("select", function(selected) {
			if(selected.value == 'jmx') {
				jmxField.show();
			}else{
				jmxField.hide();
			}

		}, this);

		this.items = [form];

		this.callParent(arguments);
	}
});
