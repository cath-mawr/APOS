package com.aposbot._default;

public interface IStaticAccess {

    public String getNpcName(int id);

    public String getNpcDesc(int id);

    public int getNpcLevel(int id);

    public String getItemName(int id);

    public String getItemDesc(int id);

    public String getItemCommand(int id);

    public int getItemBasePrice(int id);

    public boolean isItemStackable(int id);

    public boolean isItemTradable(int id);

    public String getObjectName(int id);

    public String getObjectDesc(int id);

    public String getBoundName(int id);

    public String getBoundDesc(int id);

    public int getSpellReqLevel(int id);

    public int getSpellType(int i);

    public int getReagentCount(int id);

    public int getReagentId(int spell, int i);

    public int getReagentAmount(int spell, int i);

    public int getFriendCount();

    public String getFriendName(int i);

    public int getIgnoredCount();

    public String getIgnoredName(int i);

    public int getPrayerCount();

    public int getPrayerLevel(int i);

    public String getPrayerName(int i);

    public String[] getSpellNames();

    public String[] getSkillNames();
}