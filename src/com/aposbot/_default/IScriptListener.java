package com.aposbot._default;

public interface IScriptListener {

    public void onGameTick();

    public void onPaintTick();

    public void onKeyPress(int code);

    public void onGameMessage(boolean flag, String s1, int i1, String s2, int j1, int k1, String s3, 
            String s4);

    public void onNewSleepWord();

    public void setIScript(IScript script);

    public void setScriptRunning(boolean b);

    public boolean isScriptRunning();

    public String getScriptName();

    public boolean hasScript();

	public void setBanned(boolean b);
}
