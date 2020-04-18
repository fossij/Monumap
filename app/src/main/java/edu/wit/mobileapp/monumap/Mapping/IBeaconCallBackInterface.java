package edu.wit.mobileapp.monumap.Mapping;

public interface IBeaconCallBackInterface {

    public void CorrectBeaconReached(IBeaconID id);

    public void IncorrectBeaconReached(IBeaconID id);

}
