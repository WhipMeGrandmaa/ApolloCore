package me.whipmegrandma.apollocore.enums;

public enum Operator {

	PLUS("+"),
	MINUS("-");

	private String operator;

	Operator(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return operator;
	}
}
