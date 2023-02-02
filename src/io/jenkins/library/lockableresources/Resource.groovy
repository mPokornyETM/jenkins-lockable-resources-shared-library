#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.NonNull;
import groovy.transform.Synchronized;
import io.jenkins.library.lockableresources.ResourcesManager as LRM;
import org.jenkins.plugins.lockableresources.LockableResource;

// NonCPS: since LockableResource contains transient variables, they cannot be correctly serialized
class Resource {

  private transient LockableResource resource;
  //---------------------------------------------------------------------------
  /** Returns {@code LockableResource} resource.
    @return Lockable-resource or null when does not exists.
    NonCPS because the LockableResource is not serializable.
  */
  public Resource(@NonNull String resourceName) {
    this.resource = LRM.get(resourceName);
    if (this.resource == null) {
      this.resource = new LockableResource(resourceName);
    }
  }

  //---------------------------------------------------------------------------
  public Resource(@NonNull LockableResource resource) {
    this.resource = resource;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  @Synchronized
  public synchronized void create() {
    if (LRM.resourceExists(this.name)) {
      throw new Exception();
    }
    LRM.createResource().add(this.resource);
    LRM.save();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public boolean isFree() {
    return (!this.resource.isLocked() && !this.resource.isReserved() && !this.resource.isQueued());
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public String getName() {
    return this.resource.name;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public String toString() {
    return this.getName();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public String getDescription() {
    return this.resource.description;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public void setDescription(String description) {
    this.resource.setDescription(description);
    LRM.save();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public String getNote() {
    return this.resource.note;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public void setNote(String note) {
    this.resource.setNote(note);
    LRM.save();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public boolean isEphemeral() {
    return this.resource.ephemeral;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public static List<Resource> toSafeList(@NonNull List<LockableResource> list) {
    List<Resource> ret = [];
    for(LockableResource r : list) {
      ret.push(new Resource(r));
    }
    return ret;
  }
}
