package com.example.demo.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GraphData {

	private String title;

	/**
	 * グラフ下部の分析項目、例えば項目が３つあるとすれば「一学期中間」「一学期期末」「二学期中間」等のデータ部分
	 */
	@JsonProperty("labels")
	private List<String> labelNames = new ArrayList<>();

	/**
	 * グラフの一つのデータが表す内容、例えば「Aさんの試験結果」「Aクラスの平均」等のデータpointsフィールドの題名 ともとれる
	 */
	@JsonProperty("label")
	private List<String> userNames = new ArrayList<>();

	/**
	 * 上記のuserNamesのデータ部分、獲得点等の情報がリスト形式で入る
	 */
	@JsonProperty("data")
	private List<List<Integer>> points = new ArrayList<>();

	public void addUserName(String userName) {
		this.userNames.add(userName);
	}

	public void addLabelName(String labelname) {
		this.labelNames.add(labelname);
	}

	public void addPoints(List<Integer> points) {
		this.points.add(points);
	}

}
