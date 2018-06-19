package com.aposbot._default;

public interface IPaintListener {

	public void onPaint();

	public void resetDisplayedXp();

	public boolean isPaintingEnabled();

	public void setPaintingEnabled(boolean b);

	public void setBanned(boolean b);

	public void doResize(int w, int h);

	public void setRenderTextures(boolean b);

	public void setRenderSolid(boolean b);
}
