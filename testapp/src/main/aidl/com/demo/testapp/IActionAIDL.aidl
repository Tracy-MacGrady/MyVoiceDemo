// IActionAIDL.aidl
package com.demo.testapp;

// Declare any non-default types here with import statements

interface IActionAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
  void voiceAction(int action,String voicePath);
}
