// IActionAIDL.aidl
package com.mysystem.recorder;

// Declare any non-default types here with import statements

interface IActionAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
  void voiceAction(int action,String voicePath);
}
