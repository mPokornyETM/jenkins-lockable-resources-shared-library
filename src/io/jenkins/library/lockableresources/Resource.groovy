#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.Label;
import hudson.model.labels.LabelAtom;
import java.io.Serializable;
import java.util.Collections;
// import groovy.transform.Synchronized;
import io.jenkins.library.lockableresources.ResourcesManager as RM;
import io.jenkins.library.lockableresources.ResourceLabel;
import org.jenkins.plugins.lockableresources.LockableResource;
import org.jenkins.plugins.lockableresources.LockableResourcesManager as LRM;

class Resource implements Serializable {

  private transient LockableResource resource;

  //---------------------------------------------------------------------------
  /** 
  */
  public Resource(@NonNull String resourceName) {
    this.resource = LRM.get().fromName(resourceName);
    if (this.resource == null) {
      this.resource = new LockableResource(resourceName);
    }
  }

  //---------------------------------------------------------------------------
  public Resource(@NonNull LockableResource resource) {
    this.resource = resource;
  }

  //----------------------------------------------------------------------------
  /** */
  //@Synchronized
  public void create(Map properties = null) {
    if (this.exists()) {
      throw new Exception('Resource ' + this.getName() + ' currently exists!' +
                          'Therefore can not be created.');
    }
    if (properties != null) {
      this.fromMap(properties);
    }
    LRM.get().getResources().add(this.resource);
  }

  //----------------------------------------------------------------------------
  /** */
  public void save() {
    RM.save();
  }

  //----------------------------------------------------------------------------
  /** */
  public boolean exists() {
    return RM.resourceExists(this.getName());
  }

  //----------------------------------------------------------------------------
  /** */
  public boolean isFree() {
    return (!this.resource.isLocked() && !this.resource.isReserved() && !this.resource.isQueued());
  }

  //----------------------------------------------------------------------------
  /** */
  public String getName() {
    return this.resource.name;
  }

  //----------------------------------------------------------------------------
  /** */
  public String toString() {
    return this.getName();
  }

  //----------------------------------------------------------------------------
  /** */
  public String getDescription() {
    return this.resource.description;
  }

  //----------------------------------------------------------------------------
  /** */
  public void setDescription(String description) {
    this.resource.setDescription(description);
  }

  //----------------------------------------------------------------------------
  /** */
  public String getNote() {
    return this.resource.note;
  }

  //----------------------------------------------------------------------------
  /** */
  public void setNote(String note) {
    this.resource.setNote(note);
  }

  //----------------------------------------------------------------------------
  /** */
  public boolean isEphemeral() {
    return this.resource.ephemeral;
  }

  //----------------------------------------------------------------------------
  /** */
  public List<ResourceLabel> getLabels() {
    List<ResourceLabel> list = [];
    for(String label : this.resource.labelsAsList) {
      list.push(new ResourceLabel(label));
    }
    return list;
  }

  //----------------------------------------------------------------------------
  /** */
  public void setLabels(@NonNull def labels) {
    this.resource.setLabels(Resource.toLabelsString(labels));
  }

  //----------------------------------------------------------------------------
  /** */
  public void addLabel(@NonNull ResourceLabel label) {
    if (this.resource.hasLabel(label.getName())) {
      return;
    }
    this.resource.labelsAsList.push(label.getName());
  }

  //----------------------------------------------------------------------------
  /** */
  public void removeLabel(@NonNull ResourceLabel label) {
    if (!this.resource.hasLabel(label.getName())) {
      return;
    }
    this.resource.labelsAsList.remove(label.getName());
  }

  //----------------------------------------------------------------------------
  /** */
  public boolean hasLabel(@NonNull String label) {
    return this.resource.hasLabel(label);
  }

  //----------------------------------------------------------------------------
  /** */
  public boolean hasLabel(@NonNull ResourceLabel label) {
    return hasLabel(label.getName());
  }

  //----------------------------------------------------------------------------
  /** */
  public Map toMap() {
    Map map = [
      'name' : this.getName(),
      'description' : this.getDescription(),
      'note' : this.getNote(),
      'labels' : this.getLabels(),
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
      Computer computer = jenkins.model.Jenkins.instance.getComputer(this.getName());
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

    return map;
  }

  //----------------------------------------------------------------------------
  /** */
  public Map fromMap(@NonNull Map map) {
    this.resource.setDescription(map.description);
    this.resource.setNote(map.note);
    this.resource.setLabels(Resource.toLabelsString(map.labels));
  }

  //----------------------------------------------------------------------------
  /** */
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

  public boolean matches(Label labelExpression) {
    List<String> ls = [];
    for(ResourceLabel resourceLabel : this.getLabels()) {
      ls.push(resourceLabel.getName());
    }

    return _matches(labelExpression, ls);
  }

  // NonCPS because LabelAtom is not serializable
  @NonCPS
  private boolean _matches(Label labelExpression, def _labels) {
    Collection<LabelAtom> atomLabels =  [];
    for(String resourceLabel : _labels) {
      atomLabels.push(new LabelAtom(resourceLabel));
    }
    return labelExpression.matches(atomLabels);
  }
}
