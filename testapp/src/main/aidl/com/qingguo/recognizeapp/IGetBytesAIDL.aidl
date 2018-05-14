// IMyAidlInterface.aidl
package com.qingguo.recognizeapp;

// Declare any non-default types here with import statements

interface IGetBytesAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
            void sendVoiceByte(in byte[] data);
}
