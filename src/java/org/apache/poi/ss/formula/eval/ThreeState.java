package org.apache.poi.ss.formula.eval;

public enum ThreeState {
	NULL(false),
	TRUE(true),
	FALSE(false);

	public final boolean bool;

	private ThreeState(final boolean v) {
		this.bool = v;
	}
	public static ThreeState bool(final boolean v) {
		return v ? TRUE : FALSE;
	}
}