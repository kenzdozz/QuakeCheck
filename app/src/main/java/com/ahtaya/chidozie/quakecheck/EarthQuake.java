package com.ahtaya.chidozie.quakecheck;



class EarthQuake {

    private String mMag, mPlace, mTime;

    EarthQuake(String mag, String place, String time){
        mMag = mag;
        mPlace = place;
        mTime = time;
    }

    String getmMag() { return mMag; }

    String getmPlace() { return mPlace; }

    String getmTime() { return mTime; }
}
