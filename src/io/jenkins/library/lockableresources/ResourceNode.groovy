#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;
import jenkins.model.Jenkins.instance;

class ResourceNode extends Resource {
  
  private String name;

  //---------------------------------------------------------------------------
  public ResourceNode(@NonNull String name) {
    this.name = name;
  }

  //---------------------------------------------------------------------------
  public String toString() {
    return this.name;
  }

  //---------------------------------------------------------------------------
  @NonCPS
  public boolean exists() {
    return this.getComputer() != null;
  }

  //---------------------------------------------------------------------------
  @NonCPS
  private def getComputer() {
    return jenkins.model.Jenkins.instance.getComputer(this.name);
  }
}