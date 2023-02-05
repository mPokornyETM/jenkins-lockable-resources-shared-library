#!groovy

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourceLabel;
import io.jenkins.library.lockableresources.ResourcesManager as RM;

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
@CheckForNull
Resource call(String resourceName) {
  return new Resource(RM.getResourceOrDie(resourceName));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
@CheckForNull
Resource find(String resourceName) {
  return new Resource(RM.getResource(resourceName));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
List<Resource> find(List<String> resourceNames) {
  return Resource.toSafeList(RM.getResources(resourceNames));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
List<Resource> find(ResourceLabel resourceLabel, int quantity = 0) {
  return Resource.toSafeList(RM.getResources(resourceLabel, ['quantity' : quantity]));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
List<Resource> find(ResourceLabel resourceLabel, Map opts) {
  return Resource.toSafeList(RM.getResources(resourceLabel, opts));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
List<Resource> find(int quantity, Closure closure) {
  return Resource.toSafeList(RM.getResources(closure, ['quantity' : quantity]));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
List<Resource> find(Map opts, Closure closure) {
  return Resource.toSafeList(RM.getResources(closure, opts));
}

//-----------------------------------------------------------------------------
/** Returns {@code LockableResource} resource.
  @return Lockable-resource or null when does not exists.
  NonCPS because the LockableResource is not serializable.
*/
//@NonCPS
List<Resource> find(Closure closure) {
  return Resource.toSafeList(RM.getResources(closure, ['quantity' : 0]));
}

//-----------------------------------------------------------------------------
//@NonCPS
List<Resource> getAll() {
  return Resource.toSafeList(RM.getResources());
}

//------------------------------------------------------------------------------
//@NonCPS
boolean isFree(@NonNull String resourceName) {
  Resource resource = new Resource(resourceName);
  return resource.isFree();
}

