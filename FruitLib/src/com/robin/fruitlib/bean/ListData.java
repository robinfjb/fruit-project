package com.robin.fruitlib.bean;

import java.util.List;
import java.util.Map;

public class ListData<DataType> {
	private int totalCnt;
	private List<DataType> dataset;
	private Map<String, String> extraData;
	
	/**
	 * @return the totalCnt
	 */
	public int getTotalCnt() {
		return totalCnt;
	}

	/**
	 * @return the dataset
	 */
	public List<DataType> getDataset() {
		return dataset;
	}
	
	public ListData(List<DataType> dataset) {
		super();
		this.dataset = dataset;
	}

	public ListData(int totalCnt, List<DataType> dataset) {
		super();
		this.totalCnt = totalCnt;
		this.dataset = dataset;
	}

	public ListData(int totalCnt, List<DataType> dataset,
			Map<String, String> map) {
		super();
		this.totalCnt = totalCnt;
		this.dataset = dataset;
		extraData = map;
	}

	/**
	 * @return the extraData
	 */
	public Map<String, String> getExtraData() {
		return extraData;
	}

	/**
	 * @param extraData
	 *            the extraData to set
	 */
	public void setExtraData(Map<String, String> extraData) {
		this.extraData = extraData;
	}
	
	public DataType getItem(int i) {
		return this.dataset.get(i);
	}
}