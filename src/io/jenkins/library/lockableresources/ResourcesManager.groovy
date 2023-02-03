#!groovy
package io.jenkins.library.lockableresources;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;
import io.jenkins.library.lockableresources.Resource;
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
  //@NonCPS
  @Restricted(NoExternalUse.class)
  public static LockableResource getResource(@NonNull String resourceName) {
    return LRM.get().fromName(resourceName);
  }

  //---------------------------------------------------------------------------
  /** Returns {@code LockableResource} resource.
    @return Lockable-resource or null when does not exists.
    NonCPS because the LockableResource is not serializable.
  */
  //@NonCPS
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
  //@NonCPS
  @Restricted(NoExternalUse.class)
  @CheckForNull
  public static List<LockableResource> getResources(@NonNull List<String> resourceNames) {
    List<LockableResource> retList = [];
    for (String resourceName : resourceNames) {
      retList.push(ResourcesManager.getResourceOrDie(resourceName));
    }
    return retList;
  }

  //---------------------------------------------------------------------------
  //@NonCPS
  @Restricted(NoExternalUse.class)
  public static List<LockableResource> getAllResources() {
    return LRM.get().getResources();
  }

  //---------------------------------------------------------------------------
  //@NonCPS
  @Restricted(NoExternalUse.class)
  public static void save() {
    LRM.get().save();
  }

  //---------------------------------------------------------------------------
  //@NonCPS
  @Restricted(NoExternalUse.class)
  public static boolean resourceExists(@NonNull String resourceName) {
    return ResourcesManager.getResource(resourceName) != null;
  }
}