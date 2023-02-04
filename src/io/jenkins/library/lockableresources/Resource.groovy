#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;
// import groovy.transform.Synchronized;
import io.jenkins.library.lockableresources.ResourcesManager as LRM;
import io.jenkins.library.lockableresources.ResourceLabel;
import org.jenkins.plugins.lockableresources.LockableResource;

// NonCPS: since LockableResource contains transient variables, they cannot be correctly serialized
class Resource implements Serializable {

  private transient LockableResource resource;
  private String resourceName;
  //---------------------------------------------------------------------------
  /** Returns {@code LockableResource} resource.
    @return Lockable-resource or null when does not exists.
    NonCPS because the LockableResource is not serializable.
  */
  public Resource(@NonNull String resourceName) {
    this.resourceName = resourceName;
    this.resource = LRM.getResource(resourceName);
    if (this.resource == null) {
      this.resource = new LockableResource(resourceName);
    }
  }

  //---------------------------------------------------------------------------
  public Resource(@NonNull LockableResource resource) {
    this.resource = resource;
    this.resourceName = resource.name;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  //@Synchronized
  public void create(Map properties = null) {
    if (this.exists()) {
      throw new Exception('Resource ' + $this.name + ' currently exists!' + 
                          'Therefore can not be created.');
    }
    if (properties != null) {
      this.fromMap(properties);
    }
    LRM.getAllResources().add(this.resource);
    this.save();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public void save() {
    LRM.save();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public boolean exists() {
    return LRM.resourceExists(this.name);
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
    this.save();
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
    this.save();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public boolean isEphemeral() {
    return this.resource.ephemeral;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public List<ResourceLabel> getLabels() {
    List<ResourceLabel> list;
    for(String label : this.resource.labelsAsList) {
      list.push(new ResourceLabel(label));
    }
    return list;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public void setLabels(@NonNull List<ResourceLabel> labels) {
    String labelsAsString = "";
    for(ResourceLabel label : labels) {
      if (labelsAsString != "") {
        labelsAsString += " ";
      }
      labelsAsString += label.toString();
    }
    this.resource.setLabels(labelsAsString);
    this.save();
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public Map toMap() {
    return [
      'name' : this.name,
      'description' : this.description,
      'note' : this.note,
      'labels' : this.labels,
      'isFree' : this.isFree(),
      'reservedTimestamp' : this.resource.getReservedTimestamp(),
      'isLocked' : this.resource.isLocked(),
      'lockedBy' : this.resource.getBuildName(),
      'isReserved' : this.resource.isReserved(),
      'reservedBy' : this.resource.getReservedBy(),
      'isStolen' : this.resource.isStolen(),
      'isQueued' : this.resource.isQueued()
    ];
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public Map fromMap(@NonNull Map map) {
    this.resource.description = map.description;
    this.resource.note = map.note;
    this.resource.setLabels(map.labels); //Resource.toLabelsString(map.labels);
  }

  //----------------------------------------------------------------------------
  @NonCPS
  private static String toLabelsString(labels) {
    String labelsString = "";
    if (labels == null) {
      return labelsString;
    } else if (labels instanceof String) {
      labelsString = labels;
    } else if (labels instanceof List<String>) {
      labelsString = labels.join(' ');
    } else if (labels instanceof List<ResourceLabel>) {
      for (ResourceLabel label : labels) {
        labelsString += label.name + ' ';
      }
    } else {
      throw(new Exception("Unsupported labels conversion"));
    }

    return labelsString.trim(); 
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
