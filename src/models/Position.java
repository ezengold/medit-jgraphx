package models;

import java.io.Serializable;

public class Position implements Serializable {
	private double x;
	private double y;

	public Position() {
		this.setX(0);
		this.setY(0);
	}

	public Position(double x, double y) {
		this.setX(x);
		this.setY(y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
