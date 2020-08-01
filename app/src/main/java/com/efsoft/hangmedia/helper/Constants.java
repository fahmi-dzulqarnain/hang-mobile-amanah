package com.efsoft.hangmedia.helper;

public class Constants {
    public interface ACTION {
        String STARTFOREGROUND_ACTION = "com.nkdroid.alertdialog.action.startforeground";
        String STOPFOREGROUND_PLAYER = "com.nkdroid.alertdialog.action.stopforegroundplayer";
        String STOPFOREGROUND_ACTION = "com.nkdroid.alertdialog.action.stopforeground";
        String PAUSEFOREGROUND_PLAYER = "com.nkdroid.alertdialog.action.pauseforegroundplayer";
        String PLAYFOREGROUND_PLAYER = "com.nkdroid.alertdialog.action.playforegroundplayer";

        String START_ATKJ_PLAYER = "com.efsoft.hangmedia.action.startatkjplayer";
        String PAUSE_ATKJ_PLAYER = "com.efsoft.hangmedia.action.pauseatkjplayer";
        String STOP_ATKJ_PLAYER = "com.efsoft.hangmedia.action.stopatkjplayer";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 1;
        int ATKJ_SERVICE = 29;
    }
}
