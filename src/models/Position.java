package models;

public class Position {
	private float x;
	private float y;

	public Position() {
		this.setX(0);
		this.setY(0);
	}

	public Position(float x, float y) {
		this.setX(0);
		this.setY(0);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
}
