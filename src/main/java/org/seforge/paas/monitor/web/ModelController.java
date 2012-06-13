package org.seforge.paas.monitor.web;

import java.io.FileWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;


@RequestMapping("/model")
@Controller
public class ModelController {
	
	@Autowired
	private ModelService modelService;
	
	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public ResponseEntity<String> generateModel(HttpServletRequest request) {		
		FileWriter fw;
		try {
			String path = request.getRealPath("/");		
			modelService.writeDocument(path + "/modelDiagram.xml");			
			return new ResponseEntity<String>("ok", HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("ok", HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	@RequestMapping(value = "/getmodel", method = RequestMethod.GET)
	public ResponseEntity<String> getJsonModel(HttpServletRequest request) {
		List<Phym> phyms = Phym.findAllPhyms();		
		for(Phym phym: phyms){
			for(Vim vim: phym.getVims()){
				for(AppServer as : vim.getAppServers())
//					as.checkStatus();
					as.setStatus("STARTED");
			}
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json");		
		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).deepSerialize(phyms),responseHeaders, HttpStatus.OK);
		
	}

}
