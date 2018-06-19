package com.aposbot._default;

public interface IAutoLogin {

    public void onLoginTick();

    public void onWelcomeBoxTick();

    public boolean isEnabled();

    public void setEnabled(boolean b);

    public void setAccount(String username, String password);

	public void setBanned(boolean b);

}
