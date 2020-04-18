package edu.wit.mobileapp.monumap.Mapping;

public class IBeaconID {
    private int m_MinorID;
    private int m_MajorID;

    public IBeaconID(int majorUpper, int majorLower, int minorID){
        this(majorUpper * 100 + majorLower, minorID);
    }

    public IBeaconID(int majorID, int minorID){
        if(majorID < 0 || majorID > 65535){
            throw new IllegalArgumentException("");
        }
        m_MinorID = minorID;
        m_MajorID = majorID;
    }

    public int getMajorID() {
        return m_MajorID;
    }

    public int getMinorID() {
        return m_MinorID;
    }
}
