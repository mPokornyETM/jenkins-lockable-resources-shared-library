#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;
import java.util.Collections;
import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourceLabel;
import org.jenkins.plugins.lockableresources.LockableResourcesManager as LRM;
import org.jenkins.plugins.lockableresources.LockableResource;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

// !!!!! do not use outside this library !!!!
class ResourcesManager  implements Serializable {

  //---------------------------------------------------------------------------
  /** Returns {@code LockableResource} resource.
    @return Lockable-resource or null when does not exists.
    NonCPS because the LockableResource is not serializable.
  */
  @NonCPS
  @Restricted(NoExternalUse.class)
  @CheckForNull
  public static LockableResource getResource(@NonNull String resourceName) {
    return LRM.get().fromName(resourceName);
  }

  //---------------------------------------------------------------------------
  /** Returns {@code LockableResource} resource.
    @return Lockable-resource or null when does not exists.
    NonCPS because the LockableResource is not serializable.
  */
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static LockableResource getResourceOrDie(@NonNull String resourceName) {
    LockableResource resource = ResourcesManager.getResource(resourceName);
    if (resource == null) {
      throw new Exception("Lockable resource '$resourceName' does not exist!");
    }
    return resource;
  }

  //---------------------------------------------------------------------------
  /** Returns {@code LockableResource} resource.
    @return Lockable-resource or null when does not exists.
    NonCPS because the LockableResource is not serializable.
  */
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static List<LockableResource> getResources(@NonNull List<String> resourceNames) {
    List<LockableResource> retList = [];
    for (String resourceName : resourceNames) {
      retList.push(ResourcesManager.getResourceOrDie(resourceName));
    }
    return retList;
  }

  //---------------------------------------------------------------------------
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static List<LockableResource> getResources(ResourceLabel resourceLabel, Map opts = [:]) {
    List<LockableResource> matches = LRM.get().getResourcesWithLabel(resourceLabel.name, [:]);
    return filter(matches, opts);
  }

  //---------------------------------------------------------------------------
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static List<LockableResource> getResources(Closure closure, Map opts = [:]) {
    List<LockableResource> matches = [];
    for(LockableResource resource : getAllResources()) {
      boolean match = closure(new Resource(resource));
      if (match) {
        matches.push(resource);
      }
    }
    return filter(matches, opts);
  }

  //---------------------------------------------------------------------------
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static List<LockableResource> getAllResources() {
    return LRM.get().getResources();
  }

  //---------------------------------------------------------------------------
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static void save() {
    LRM.get().save();
  }

  //---------------------------------------------------------------------------
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static boolean resourceExists(@NonNull String resourceName) {
    return ResourcesManager.getResource(resourceName) != null;
  }

  
  //---------------------------------------------------------------------------
  @NonCPS
  private static List<LockableResource> filter(List<LockableResource> allMatches, Map opts) {
    if (opts == null) {
      opts = [:];
    }

    final int expectedCount = opts.expectedCount != null ? opts.expectedCount : 0;
    final int minCount = opts.minCount != null ? opts.minCount : expectedCount;

    if (expectedCount == 0) {
      return allMatches;
    }

    if (expectedCount < allMatches.size()) {
      throw(new Exception("You has expected $expectedCount, but there are currently only $allMatches.size"));
    }

    if (opts.randomize != null) {
      Collections.shuffle(allMatches);
    }

    if (opts.orderBy != null) {
      allMatches = sort(allMatches, opts.orderBy);
    }
    
    List<LockableResource> retList = [];
    for(int i = 0; i < expectedCount; i++) {
      retList.push(allMatches[i]);
    }

    return retList;
  }

  //---------------------------------------------------------------------------
  @NonCPS
  private static void sort(List<LockableResource> resources, def orderBy) {
    // get current state and property of resources to eliminate
    // java.lang.IllegalArgumentException: Comparison method violates its general contract!
    // otherwise nobody can grant, that the resource state/property has been not changed
    List<Map> list = [];

    for (LockableResource resource : resources) {
      list.push(new Resource(resource).toMap());
    }

    def orderByDef = new OrderBy(orderBy);
    list.sort(orderByDef);

    resources = [];
    for (Map map : list) {
      resources.add(new LockableResource(map.name));
    }
  }
}