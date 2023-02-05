#!groovy
package io.jenkins.library.lockableresources;

class Utils {
  //---------------------------------------------------------------------------
  public static void fixNullMap(Map map) {
    if (map == null) {
      map = [:];
    }
  }
}