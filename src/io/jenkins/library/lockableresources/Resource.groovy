#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.Label;
import hudson.model.labels.LabelAtom;
import java.io.Serializable;
import java.util.Collections;
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
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public boolean isEphemeral() {
    return this.resource.ephemeral;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public List<ResourceLabel> getLabels() {
    List<ResourceLabel> list = [];
    for(String label : this.resource.labelsAsList) {
      list.push(new ResourceLabel(label));
    }
    return list;
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public void setLabels(@NonNull def labels) {
    this.resource.setLabels(Resource.toLabelsString(labels));
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public void addLabel(@NonNull ResourceLabel label) {
    if (this.resource.hasLabel(label.getName())) {
      return;
    }
    this.resource.labelsAsList.push(label.getName());
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public void removeLabel(@NonNull ResourceLabel label) {
    if (!this.resource.hasLabel(label.getName())) {
      return;
    }
    this.resource.labelsAsList.remove(label.getName());
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public boolean hasLabel(@NonNull String label) {
    return this.resource.hasLabel(label);
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public boolean hasLabel(@NonNull ResourceLabel label) {
    return hasLabel(label.getName());
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public Map toMap() {
    Map map = [
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

    if (this.hasLabel(ResourceLabel.NODE_LABEL)) {
      Computer computer = jenkins.model.Jenkins.instance.getComputer(nodeName);
      if (computer != null) {
        Map compMap = [:];
        compMap['isOnline'] = computer.isOnline();
        // object of OfflineCause or null
        compMap['offlineCause'] = computer.getOfflineCause();
        // The time when this computer last became idle.
        compMap['idleStartMilliseconds'] = computer.getIdleStartMilliseconds();
        // true if this computer has some idle executors that can take more workload
        compMap['isPartiallyIdle'] = computer.isPartiallyIdle();
        // true if all the executors of this computer are idle.
        compMap['isIdle'] = computer.isPartiallyIdle();
        // The current size of the executor pool for this computer.
        compMap['countExecutors'] = computer.countExecutors();
        // The number of executors that are doing some work right now.
        compMap['countBusy'] = computer.countBusy();
        // The number of idle {@link Executor}s that can start working immediately.
        compMap['countIdle'] = computer.countIdle();

        map['node'] = compMap;
      }
    }
  }

  //----------------------------------------------------------------------------
  @NonCPS
  public Map fromMap(@NonNull Map map) {
    this.resource.setDescription(map.description);
    this.resource.setNote(map.note);
    this.resource.setLabels(Resource.toLabelsString(map.labels));
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
      throw(new Exception("Unsupported labels conversion: " + labels.class.name + " " + labels));
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

  //@Override
  //public boolean equals(Object obj) {
  //  if (this == obj) return true;
  //  if (obj == null) return false;
  //  if (getClass() != obj.getClass()) return false;
  //  Resource other = (Resource) obj;
  //  if (name == null) {
  //    if (other.name != null) return false;
  //  } else if (!name.equals(other.name)) return false;
  //  return true;
  //}

  @NonCPS
  public boolean matches(Label labelExpression) {
    Collection<LabelAtom> atomLabels =  [];
    for(ResourceLabel resourceLabel : this.getLabels()) {
      atomLabels.push(new LabelAtom(resourceLabel.getName()));
    }
    return labelExpression.matches(atomLabels);
  }
}
