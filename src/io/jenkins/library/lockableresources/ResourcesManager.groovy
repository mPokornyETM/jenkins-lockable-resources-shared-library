#!groovy
package io.jenkins.library.lockableresources

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.plugins.lockableresources.ResourcesManager
import io.jenkins.plugins.lockableresources.LockableResource
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

// !!!!! do not use outside this library !!!!
class ResourcesManager {

  //---------------------------------------------------------------------------
  /** Returns {@code LockableResource} resource.
    @return Lockable-resource or null when does not exists.
    NonCPS because the LockableResource is not serializable.
  */
  @NonCPS
  @Restricted(NoExternalUse.class)
  public static LockableResource getResource(@NonNull String resourceName) {
    return LRM.fromName(resourceName);
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
  @CheckForNull
  public static List<LockableResource> getResources(@NonNull List<String> resourceNames) {
    List<LockableResource> retList = [];
    for (String resourceName : resourceNames) {
      retList.push(ResourcesManager.getResourceOrDie(resourceName));
    }
    return retList;
  }

  //---------------------------------------------------------------------------
  @Restricted(NoExternalUse.class)
  public static List<LockableResource> getAllResource() {
    return LRM.getResources();
  }

  //---------------------------------------------------------------------------
  @Restricted(NoExternalUse.class)
  public static void save() {
    LRM.save();
  }

  //---------------------------------------------------------------------------
  @Restricted(NoExternalUse.class)
  public static boolean resourceExists(@NonNull String resourceName) {
    return ResourcesManager.getResource(resourceName) != null;
  }
}