package org.seforge.paas.monitor.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.extjs.TreeNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

@RequestMapping("/monitees/**")
@Controller
public class MoniteeController {

    @RequestMapping
    public void get(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

    @RequestMapping
    public String index() {
        return "monitees/index";
    }
    
    @RequestMapping(params = "node", method = RequestMethod.GET)
	public ResponseEntity<String> listJsonTree(
			@RequestParam("node") String nodeId) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
		List<TreeNode> response = null;
		try {
			List<Phym> records = Phym.findAllPhyms();
			// If these is no record now
			if (records.size() <= 0) {
				response = new ArrayList<TreeNode>();
				TreeNode root = new TreeNode("There are no monitee now");
				root.setLeaf(true);
				response.add(root);
				returnStatus = HttpStatus.OK;
			}
			// There are phyms in the db
			else {
				if (nodeId.equals("root")) {
					response = new ArrayList<TreeNode>();
					for (Phym phym : records) {
						if(phym.getIsMonitee()){
							TreeNode phymNode = new TreeNode();
							phymNode.setText(phym.getName());
							phymNode.setId("phym" + phym.getId().toString());
							phymNode.setLeaf(false);
							phymNode.setExpanded(false);
							response.add(phymNode);							
						}						
					}
				} else if (nodeId.indexOf("phym") != -1) {
					Long id = Long.valueOf(nodeId.substring(nodeId
							.indexOf("phym") + 4));
					Phym phym = Phym.findPhym(id);
					Set<Vim> vims = phym.getVims();
					if (vims.size() > 0) {
						response = new ArrayList<TreeNode>();
						for (Vim vim : vims) {
							if(vim.getIsMonitee()){
								TreeNode vimNode = new TreeNode();
								vimNode.setText(vim.getName());
								vimNode.setId("vim" + vim.getId().toString());
								vimNode.setLeaf(false);
								response.add(vimNode);								
							}							
						}
					} else {
						response = new ArrayList<TreeNode>();
						TreeNode vimNode = new TreeNode();
						vimNode.setText("There is no Virtual Machines");
						vimNode.setLeaf(true);
						response.add(vimNode);
					}

				} else if (nodeId.indexOf("vim") != -1) {
					Long id = Long.valueOf(nodeId.substring(nodeId
							.indexOf("vim") + 3));
					Vim vim = Vim.findVim(id);
					List<AppServer> appServers = AppServer.findAppServersByIp(vim.getIp()).getResultList();
					if (appServers.size() > 0) {
						response = new ArrayList<TreeNode>();
						for (AppServer appServer : appServers) {
							if(appServer.getIsMonitee()){
								TreeNode appServerNode = new TreeNode();
								appServerNode.setText(appServer.getName());
								appServerNode.setId("appServer" + appServer.getId().toString());
								appServerNode.setLeaf(false);
								response.add(appServerNode);								
							}							
						}
					} else {
						response = new ArrayList<TreeNode>();
						TreeNode appServerNode = new TreeNode();
						appServerNode.setText("There is no App Servers");
						appServerNode.setLeaf(true);
						response.add(appServerNode);
					}

				} else if (nodeId.indexOf("appServer") != -1) {
					Long id = Long.valueOf(nodeId.substring(nodeId
							.indexOf("appServer") + 9));
					AppServer appServer = AppServer.findAppServer(id);
					List<AppInstance> appInstances = AppInstance.findAppInstancesByAppServer(appServer).getResultList();
					if (appInstances.size() > 0) {
						response = new ArrayList<TreeNode>();
						for (AppInstance appInstance : appInstances) {
							if(appInstance.getIsMonitee()){
								TreeNode appInstanceNode = new TreeNode();
								appInstanceNode.setText(appInstance.getName());
								appInstanceNode.setId("appInstance" + appInstance.getId().toString());
								appInstanceNode.setLeaf(true);							
								response.add(appInstanceNode);
							}							
						}
					} else {
						response = new ArrayList<TreeNode>();
						TreeNode appInstanceNode = new TreeNode();
						appInstanceNode.setText("There is no App Instances");
						appInstanceNode.setLeaf(true);
						response.add(appInstanceNode);
					}

				}
				returnStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
		}

		// Return list of retrieved performance areas
		return new ResponseEntity<String>(new JSONSerializer()
				.exclude("*.class")
				.transform(new DateTransformer("MM/dd/yy"), Date.class)
				.serialize(response), returnStatus);
	}
    
    
}
