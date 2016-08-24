package com.hust.model.po;

public class InfoType {

	private int id;
	private String name;
	private int weight;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "InfoType [id=" + id + ", name=" + name + ", weight=" + weight + "]";
	}

}
