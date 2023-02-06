#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;
import java.util.Collections;
import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourceLabel;
import io.jenkins.library.lockableresources.Utils;
import org.jenkins.plugins.lockableresources.LockableResourcesManager as LRM;
import org.jenkins.plugins.lockableresources.LockableResource;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

// !!!!! do not use outside this library !!!!
class ResourcesManager  implements Serializable {

  //---------------------------------------------------------------------------
  /**
  */
  /** */
  @Restricted(NoExternalUse.class)
  @CheckForNull
  public static Resource getResource(@NonNull String resourceName) {
    def r = LRM.get().fromName(resourceName);
    if (r == null) {
      return null;
    }
    return new Resource(r);
  }

  //---------------------------------------------------------------------------
  /**
  */
  /** */
  @Restricted(NoExternalUse.class)
  public static Resource getResourceOrDie(@NonNull String resourceName) {
    Resource resource = ResourcesManager.getResource(resourceName);
    if (resource == null) {
      throw new Exception("Lockable resource '$resourceName' does not exist!");
    }
    return resource;
  }

  //---------------------------------------------------------------------------
  /**
  */
  /** */
  @Restricted(NoExternalUse.class)
  public static List<Resource> getResources(@NonNull List<String> resourceNames) {
    List<Resource> retList = [];
    for (String resourceName : resourceNames) {
      retList.push(ResourcesManager.getResourceOrDie(resourceName));
    }
    return retList;
  }

  //---------------------------------------------------------------------------
  /** */
  @Restricted(NoExternalUse.class)
  public static List<Resource> getResources(ResourceLabel resourceLabel, Map opts = [:]) {
    return reOrder(toSafeList(LRM.get().getResourcesWithLabel(resourceLabel.name, [:])), opts);
  }

  //---------------------------------------------------------------------------
  /** */
  @Restricted(NoExternalUse.class)
  public static List<Resource> getResources(Closure closure, Map opts = [:]) {
    opts = Utils.fixNullMap(opts);
    List<Resource> matches = [];

    for(Resource resource : getAllResources()) {
      boolean match = closure(resource);
      if (match) {
        matches.push(resource);
      }
    }

    return reOrder(matches, opts);
  }

  //---------------------------------------------------------------------------
  /** */
  @Restricted(NoExternalUse.class)
  public static List<Resource> getAllResources() {
    return toSafeList(LRM.get().getResources());
  }

  //---------------------------------------------------------------------------
  /** */
  @Restricted(NoExternalUse.class)
  public static void save() {
    LRM.get().save();
  }

  //---------------------------------------------------------------------------
  /** */
  @Restricted(NoExternalUse.class)
  public static boolean resourceExists(@NonNull String resourceName) {
    return LRM.get().fromName(resourceName) != null;
  }


  //---------------------------------------------------------------------------
  /** */
  private static List<Resource> reOrder(List<Resource> allMatches, Map opts) {
    opts = Utils.fixNullMap(opts);

    final int quantity = opts.quantity != null ? opts.quantity : 0;
    final int minCount = opts.minCount != null ? opts.minCount : quantity;

    if ((quantity > 0) && (minCount > quantity)) {
      throw(new Exception("Parameter mismatch minCount $minCount vs quantity $quantity"));
    }
    if (minCount > allMatches.size()) {
      throw(new Exception("You has expected $quantity resource(s), but there are currently only $allMatches.size"));
    }

    if (opts.randomize != null) {
      Collections.shuffle(allMatches);
    }

    if (opts.orderBy != null) {
      allMatches = sort(allMatches);
    }

    if (quantity == 0) {
      // return all possible resources
      return allMatches;
    }

    List<Resource> retList = [];
    for(int i = 0; i < quantity; i++) {
      retList.push(allMatches[i]);
    }

    return retList;
  }

  //---------------------------------------------------------------------------
  private static List<Resource> sort(List<Resource> resources) {
    // get current state and property of resources to eliminate
    // java.lang.IllegalArgumentException: Comparison method violates its general contract!
    // otherwise nobody can grant, that the resource state/property has been not changed
    // during sorting
    // It is only sporadic issue, but it will abort our build
    List<Map> list = [];

    for (Resource resource : resources) {
      list.push(resource.toMap());
    }

    // in extra function, because of NonCPS
    _sort(list);

    resources = [];
    for (Map map : list) {
      resources.add(new Resource(map.name));
    }

    return resources;
  }

  //----------------------------------------------------------------------------
  // NonCps because sort is NON-CPS. See https://issues.jenkins.io/browse/JENKINS-44924
  @NonCPS
  private static _sort(List<Map>list) {
    list.sort(new OrderBy([
        { !it.isFree },
        // all free nodes first
        { it.node != null && !it.node.isOnline },
        // 0 executors means, there is something running
        { it.node != null ? -it.node.countIdle : null },
        // keep last idle node on the end
        { it.node != null ? it.node.idleStartMilliseconds : null }
      ])
    );
  }


  //----------------------------------------------------------------------------
  //maybe @NonCPS
  public static List<Resource> toSafeList(@NonNull List<LockableResource> list) {
    List<Resource> ret = [];
    for(LockableResource r : list) {
      ret.push(new Resource(r));
    }
    return ret;
  }
}