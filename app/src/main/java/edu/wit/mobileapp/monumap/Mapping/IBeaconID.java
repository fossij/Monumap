package edu.wit.mobileapp.monumap.Mapping;

import java.io.Serializable;
import java.util.Objects;

public class IBeaconID implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IBeaconID iBeaconID = (IBeaconID) o;
        return m_MinorID == iBeaconID.m_MinorID &&
                m_MajorID == iBeaconID.m_MajorID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_MinorID, m_MajorID);
    }

    public int getMajorID() {
        return m_MajorID;
    }

    public int getMinorID() {
        return m_MinorID;
    }
}
