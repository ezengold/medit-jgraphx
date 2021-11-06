package ui;

public enum GraphStyles {
	FILL_COLOR("#74b8e8"), STROKE_COLOR("#0e79c4"), FONT_COLOR("#084875"), ACTIVE_FILL_COLOR("#71e390"),
	ACTIVE_STROKE_COLOR("#055e1d"), ACTIVE_FONT_COLOR("#055e1d"), INIT_FILL_COLOR("084875"),
	INIT_STROKE_COLOR("#042238"), INIT_FONT_COLOR("#FFFFFF"), ACTIVE_EDGE_STROKE_COLOR("#22a144");

	private String value = "";

	GraphStyles(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
