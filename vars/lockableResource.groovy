#!groovy

import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourcesManager as RM;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
@NonCPS
@CheckForNull
Resource call(String resourceName) {
  return new Resource(RM.getResourceOrDie(resourceName));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
@NonCPS
@CheckForNull
Resource find(String resourceName) {
  return new Resource(RM.getResource(resourceName));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
@NonCPS
List<Resource> find(List<String> resourceNames) {
  return Resource.toSafeList(RM.getResources(resourceNames));
}

//-----------------------------------------------------------------------------
@NonCPS
List<Resource> getAll() {
  return Resource.toSafeList(RM.getResources());
}

//------------------------------------------------------------------------------
@NonCPS
boolean isFree(@NonNull String resourceName) {
  Resource resource = new Resource(resourceName);
  return resource.isFree();
}

//------------------------------------------------------------------------------
@NonCPS
void _lock(@NonNull Resource resource, @NonNull Map opts, @NonNull def closure) {
  if (opts.beforeLock != null) {
    opts.beforeLock();
  }
  echo("Try to lock resource: $resource.name");
  lock(resource.getName()) {
    if (opts.onLock != null) {
      opts.onLock();
    }
    closure();
    if (opts.beforeRelease != null) {
      opts.beforeRelease();
    }
  }
  if (opts.afterRelease != null) {
    opts.afterRelease();
  }
}

//------------------------------------------------------------------------------
@NonCPS
void _lock(@NonNull String resourceName, @NonNull Map opts, @NonNull def closure) {
  _lock(new Resource(resourceName), opts, closure);
}

