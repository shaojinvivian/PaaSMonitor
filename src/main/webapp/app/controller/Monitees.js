Ext.define('PaaSMonitor.controller.Monitees', {
	extend : 'Ext.app.Controller',
	stores : ['Phyms', 'Vims', 'MoniteeTree', 'AppServers', 'AppInstances'],
	models : ['Phym', 'Vim', 'AppServer', 'AppInstance'],
	views : ['monitee.AddPhym', 'monitee.AddVims', 'monitee.ListVimsByPhym', 'monitee.ViewMonitees', 'monitee.AddAppServer', 'monitee.ChooseAppServer', 'monitee.ListAppServers', 'monitee.AddAppInstance', 'monitee.ListAppInstances'],

	init : function() {
		this.control({
			'addPhym button[action=save]' : {
				click : this.savePhym
			},
			'addAppServer button[action=save]' : {
				click : this.saveAppServer
			},
			'addVims button' : {
				click : this.saveVims
			},
			'addAppServer' : {
				beforeactivate : this.onAddAppServerActivated				
			},
			'chooseAppServer button' : {
				click : this.chooseAppServers
			},
			'viewMonitees' : {
				beforeactivate : this.onViewMoniteesActivated
			},
			'addAppInstance button' : {
				click : this.saveAppInstances
			}
		});
	},
	savePhym : function(button) {
		var form = button.up('form'), values = form.getValues(), panel = form.up('panel');
		uppanel = panel.up('panel');
		var phym = Ext.ModelManager.create(values, 'PaaSMonitor.model.Phym');
		var store = this.getVimsStore();
		var treeStore = this.getMoniteeTreeStore();
		phym.save({
			success : function(phym, operation) {
				form.getForm().reset();
				store.getProxy().extraParams = {
					findVims : "ByPhym",
					phymId : phym.internalId
				};
				store.load();
				Ext.ComponentManager.get('add_vims-panel').setTitle('Phym ' + phym.get('ip'));
				uppanel.layout.setActiveItem('add_vims-panel');
				store.getProxy().extraParams = {};
				treeStore.load();
			}
		});
	},
	saveAppServer : function(button) {
		var form = button.up('form'), values = form.getValues(), panel = form.up('panel');
		uppanel = panel.up('panel');
		var appServer = Ext.ModelManager.create(values, 'PaaSMonitor.model.AppServer');
		var store = this.getVimsStore();
		var treeStore = this.getMoniteeTreeStore();
		appServer.save({
			success : function(appServer, operation) {
				form.getForm().reset();
				/*
				 store.getProxy().extraParams = {
				 findVims : "ByPhym",
				 phymId : phym.internalId
				 };
				 store.load();
				 Ext.ComponentManager.get('add_vims-panel').setTitle('Phym ' + phym.get('ip'));
				 uppanel.layout.setActiveItem('add_vims-panel');
				 store.getProxy().extraParams = {};
				 treeStore.load();
				 */
			}
		});
	},
	saveVims : function(button) {
		this.getVimsStore().sync();
		var panel = button.up('panel').up('panel');
		Ext.MessageBox.alert('Success', 'You have successfully added monitees', function() {
			panel.layout.setActiveItem('start-panel');
		});
	},
	chooseAppServers : function(button) {
		var serverStore = this.getAppServersStore();
		serverStore.sync();
		serverStore.filter('isMonitee', true);
		var idList = new Array();
		var server;
		serverStore.each(function(record) {
			idList.push(record.get("id"));
		});
		var instanceStore = this.getAppInstancesStore();
		instanceStore.getProxy().extraParams = {
			findAppInstances : "ByAppServers",
			appServerIdList : idList
		};
		instanceStore.load();

		instanceStore.getProxy().extraParams = {};

		var panel = button.up('panel').up('panel').up('panel');
		panel.layout.setActiveItem('add_appinstance-panel');
	},
	saveAppInstances : function(button) {
		this.getAppInstancesStore().sync();
		var panel = button.up('panel').up('panel');
		Ext.MessageBox.alert('Success', 'You have successfully added monitees', function() {
			panel.layout.setActiveItem('start-panel');
		});
	},
	onAddAppServerActivated : function(panel) {
		var serverStore = this.getAppServersStore();
		serverStore.load();
		if(serverStore.getCount() <= 0) {
			panel.down('panel').hide();
			panel.down('#box').show();

		} else {
			panel.down('panel').show();
			panel.down('#box').hide();

		}
	},
	onViewMoniteesActivated : function() {
		this.getMoniteeTreeStore().load();
	}
});
