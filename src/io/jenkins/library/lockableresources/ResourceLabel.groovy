#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;

class ResourceLabel implements Serializable {

  public static final String NODE_LABEL = 'node';

  private String name;

  //---------------------------------------------------------------------------
  public ResourceLabel(@NonNull String name) {
    this.name = name;
  }

  //---------------------------------------------------------------------------
  //@NonCPS
  public String toString() {
    return this.name;
  }

  //---------------------------------------------------------------------------
  //@NonCPS
  static int inc = 0;
  public String getName() {
    if ( inc > 10)
    throw new Exception('How?')
    inc++
    return this.name;
  }
}