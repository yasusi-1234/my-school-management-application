package com.example.demo.rest.service;

import com.example.demo.rest.form.DeviationGraphConfig;
import com.example.demo.rest.form.GraphConfig;
import com.example.demo.rest.form.StudentGraphConfig;
import com.example.demo.rest.model.DeviationGraphData;
import com.example.demo.rest.model.GraphData;
import com.example.demo.rest.model.StudentDeviationGraphConfig;

public interface GraphService {

	GraphData createGradeGraphData(GraphConfig graphConfig);

	/**
	 * 標準偏差用グラフデータを作成し、返却するメソッド
	 */
	DeviationGraphData createDeviationGraphData(DeviationGraphConfig deviationGraphConfig);

	DeviationGraphData createStudentDeviationGraphData(StudentDeviationGraphConfig studentDeviationGraphConfig,
			Long studentId);

	/**
	 * 生徒用のグラフデータを返却するメソッド
	 */
	GraphData createIndividualGraphData(Long studentId, StudentGraphConfig graphConfig);

}
