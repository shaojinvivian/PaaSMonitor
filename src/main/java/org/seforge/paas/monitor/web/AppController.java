package org.seforge.paas.monitor.web;

import org.seforge.paas.monitor.domain.App;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebJson(jsonObject = App.class)
@Controller
@RequestMapping("/apps")
public class AppController {
}
