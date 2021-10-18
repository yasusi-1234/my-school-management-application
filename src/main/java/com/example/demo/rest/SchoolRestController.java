package com.example.demo.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.grade.repository.UserGradeClassRepository;
import com.example.demo.domain.test.repository.UserTestRepository;
import com.example.demo.rest.form.DeviationGraphConfig;
import com.example.demo.rest.form.GraphConfig;
import com.example.demo.rest.form.StudentGraphConfig;
import com.example.demo.rest.model.DeviationGraphData;
import com.example.demo.rest.model.GraphData;
import com.example.demo.rest.model.StudentDeviationGraphConfig;
import com.example.demo.rest.service.GraphService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("rest/school")
public class SchoolRestController {

	private final GraphService graphService;

	private final UserTestRepository repository;

	private final UserGradeClassRepository ugcRepository;

	@PostMapping("/graph/{studentId}")
	public GraphData getIndividualRadarData(@PathVariable("studentId") Long studentId,
			@RequestBody StudentGraphConfig graphConfig) {
		System.out.println("studentId : " + studentId + " gaphConfig: " + graphConfig);
		return graphService.createIndividualGraphData(studentId, graphConfig);
	}

	@PostMapping(path = "/graph")
	public GraphData test(@RequestBody GraphConfig graphConfig) {
		System.out.println(graphConfig);
		return graphService.createGradeGraphData(graphConfig);
	}

	@PostMapping(path = "/graph/sample")
	public DeviationGraphData test2(@RequestBody DeviationGraphConfig deviationGraphConfig) {
		System.out.println(deviationGraphConfig);
		return graphService.createDeviationGraphData(deviationGraphConfig);
	}

	@PostMapping(path = "/graph/sample/{studentId}")
	public DeviationGraphData test2(@RequestBody StudentDeviationGraphConfig studentDeviationGraphConfig,
			@PathVariable("studentId") Long studentId) {
		return graphService.createStudentDeviationGraphData(studentDeviationGraphConfig, studentId);
	}

}
