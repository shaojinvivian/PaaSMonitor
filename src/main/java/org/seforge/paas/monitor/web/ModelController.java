package org.seforge.paas.monitor.web;

import java.io.FileWriter;
import javax.servlet.http.HttpServletRequest;
import org.seforge.paas.monitor.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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

}
