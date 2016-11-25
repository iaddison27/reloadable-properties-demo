package org.test.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.test.config.PropertiesConfig;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {

	@Autowired
	private PropertiesConfig properties;

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> list() {
		int numResults = properties.getResultsCount();
		int resultLength = properties.getIndividualResultLength();
		List<String> results = new ArrayList<>(numResults);
		for (int i = 0; i < numResults; i++) {
			StringBuilder result = new StringBuilder();
			for (int j = 0; j < resultLength; j++) {
				result.append("A");
			}
			results.add(result.toString());
		}
		return results;
	}

}
