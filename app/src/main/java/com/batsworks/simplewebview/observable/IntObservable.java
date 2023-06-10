package com.batsworks.simplewebview.observable;


import java.io.Serializable;
import java.util.Observable;

public class IntObservable extends Observable implements Serializable {

    private boolean aBoolean = false;

    public Boolean haveChanged() {
        return aBoolean;
    }

    public void setChange(Boolean aBoolean) {
        this.aBoolean = aBoolean;
        this.setChanged();
        notifyObservers(aBoolean);
    }

}
