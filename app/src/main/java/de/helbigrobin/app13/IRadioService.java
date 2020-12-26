package de.helbigrobin.app13;

public interface IRadioService{
    RadioService.RadioState getState();
    void startRadio();
    void stopRadio();
    void pauseRadio();
    void continueRadio();
}
