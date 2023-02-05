#!groovy
package io.jenkins.library.lockableresources;

class Utils {

  //---------------------------------------------------------------------------
  @NonCPS
  public static Map fixNullMap(Map map) {
    if (map == null) {
      return [:];
    }
    return map;
  }
}