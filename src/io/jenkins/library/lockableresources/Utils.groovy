#!groovy
package io.jenkins.library.lockableresources;

class Utils {

  public static def globalScope = null;

  //---------------------------------------------------------------------------
  @NonCPS
  public static Map fixNullMap(Map map) {
    if (map == null) {
      return [:];
    }
    return map;
  }

  //---------------------------------------------------------------------------
  @NonCPS
  public static echo(String msg) {
    if (globalScope == null) {
      return;
    }
    globalScope.echo(msg);
  }
}