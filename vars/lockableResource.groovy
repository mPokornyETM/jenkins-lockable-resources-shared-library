#!groovy

import org.jenkins.plugins.lockableresources.LockableResourcesManager as LRM

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
@NonCPS
@CheckForNull
LockableResource call(String resourceName) {
  return get(String name);
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
@NonCPS
@CheckForNull
LockableResource get(String resourceName) {
  return LRM.fromName(name);
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
@NonCPS
LockableResource getOrDie(String resourceName) {
  LockableResource resource = get(resourceName);
  if (resource == null) {
    throw new Exception("Lockable resource '$resourceName' does not exist!");
  }
  return resource;
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
@NonCPS
LockableResource get(List<String> resourceNames) {

  List<LockableResource> retList = [];
  for (resourceName : resourceNames) {
    LockableResource resource = get(resourceName);
    if (resource != null)
      retList.push(resource);
  }
  return retList;
}

//-----------------------------------------------------------------------------
@NonCPS
List<LockableResource> getAll() {
  return LRM.getResources();
}

//------------------------------------------------------------------------------
@NonCPS
boolean isFree(@NonNull String resourceName) {
  LockableResource resource = get(resourceName);
  if (resource == null) {
    throw new Exception("Lockable resource '$resourceName' does not exist!");
  }
  return isFree(resource);
}

//------------------------------------------------------------------------------
@NonCPS
boolean isFree(@NonNull LockableResource resource) {
  return (!resource.isLocked() && !resource.isReserved() && !resource.isQueued());
}

//------------------------------------------------------------------------------
@NonCPS
boolean isFree(@NonNull List<String> resourceNames) {
  LockableResource resource = get(resourceName);
  if (resource == null) {
    throw new Exception("Lockable resource '$resourceName' does not exist!");
  }
  return (!resource.isLocked() && !resource.isReserved() && !resource.isQueued());
}

