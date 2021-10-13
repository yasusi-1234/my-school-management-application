package com.example.demo.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.form.FormEnum.Subject;
import com.example.demo.domain.grade.model.propagation.UserGradeClassView;
import com.example.demo.domain.grade.repository.UserGradeClassRepository;
import com.example.demo.domain.test.model.propagation.AllClassAndSubjectView;
import com.example.demo.domain.test.model.propagation.AllClassOneSubjectView;
import com.example.demo.domain.test.model.propagation.AnySeasonAndAllSubjectView;
import com.example.demo.domain.test.model.propagation.AnySeasonTestAverageView;
import com.example.demo.domain.test.model.propagation.OneSeasonTestAverageView;
import com.example.demo.domain.test.model.propagation.OneStudentView;
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

	@GetMapping(path = "/test")
	public ResponseEntity<Object> test() {
		Subject.ALL.valuesExcludeAll().stream().map(s -> s.getSubjectName()).sorted(String::compareTo)
				.forEach(s -> System.out.println(s));
		List<OneSeasonTestAverageView> res = repository.findOneSeasonTestAverage(2021, (byte) 1, "A", "1学期中間");
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test1")
	public ResponseEntity<Object> test1() {
		List<OneSeasonTestAverageView> res = repository.findOneSeasonTestAverage(2021, (byte) 1, "1学期中間");
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test2")
	public ResponseEntity<Object> test2() {
		List<AnySeasonTestAverageView> res = repository.findAnySeasonTestAverage(2021, (byte) 1, "A", "英語",
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test3")
	public ResponseEntity<Object> test3() {
		List<AnySeasonTestAverageView> res = repository.findAnySeasonTestAverage(2021, (byte) 1, "英語",
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test4")
	public ResponseEntity<Object> test4() {
		List<AnySeasonAndAllSubjectView> res = repository.findAnySeasonAndAllSubjects(2021, (byte) 1, "A",
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test5")
	public ResponseEntity<Object> test5() {
		List<AnySeasonAndAllSubjectView> res = repository.findAnySeasonAndAllSubjects(2021, (byte) 1,
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test6")
	public ResponseEntity<Object> test6() {
		List<AllClassOneSubjectView> res = repository.findAllClassOneSubjects(2021, (byte) 1, "地理",
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test7")
	public ResponseEntity<Object> test7() {
		List<AllClassAndSubjectView> res = repository.findAllClassAndSubjects(2021, (byte) 1,
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test8")
	public ResponseEntity<Object> test8() {
		List<OneStudentView> res = repository.findOneSeasonTestStudent((byte) 1, 16L, "1学期中間");
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test9")
	public ResponseEntity<Object> test9() {
		List<OneStudentView> res = repository.findAnySeasonAllSubjectTestStudent((byte) 1, 16L,
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test10")
	public ResponseEntity<Object> test10() {
		List<OneStudentView> res = repository.findAnySeasonOneSubjectTestStudent((byte) 1, 16L, "国語",
				Arrays.asList("1学期中間", "1学期期末"));
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test11/{userName}")
	public ResponseEntity<Object> test11(@PathVariable String userName) {
		UserGradeClassView res = ugcRepository.findUserGradeGlassView(userName);
		log.info("year: {}, grade: {}, clazzName: {}", res.getYear(), res.getGrade(), res.getClazzName());
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test12")
	public ResponseEntity<Object> test12() {
		List<String> res = repository.findAllSeasonByYear(2021);
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/test13")
	public ResponseEntity<Object> test13() {
		List<String> res = repository.findAllSeasonByYear(2021, (byte) 1, "A");
		List<String> test = new ArrayList<>(Arrays.asList("1学期中間", "1学期期末", "2学期中間", "2学期期末", "3学期中間", "3学期期末"));
		res.sort(Comparator.naturalOrder());
		log.info("naturalOrder: {}", res.toString());
		res.sort(Comparator.reverseOrder());
		log.info("reverseOrder: {}", res.toString());
		log.info("*******************************");
		test.sort(Comparator.naturalOrder());
		log.info("naturalOrder: {}", test.toString());
		test.sort(Comparator.reverseOrder());
		log.info("reverseOrder: {}", test.toString());
		return ResponseEntity.ok(res);
	}

}
