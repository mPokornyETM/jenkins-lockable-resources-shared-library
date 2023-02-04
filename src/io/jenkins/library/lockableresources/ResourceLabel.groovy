#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;

class ResourceLabel implements Serializable {
  
  private String name;

  //---------------------------------------------------------------------------
  public ResourceLabel(@NonNull String name) {
    this.name = name;
  }

  //---------------------------------------------------------------------------
  public String toString() {
    return this.name;
  }

  //---------------------------------------------------------------------------
  public String getName() {
    return this.name;
  }
}