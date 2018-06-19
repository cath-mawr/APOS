package com.aposbot._default;

import com.aposbot.BotLoader;

public interface ISleepListener {

    public void setSolver(BotLoader bl, String type);

    public void onNewWord(byte[] data);

    public String getGuess();
}
