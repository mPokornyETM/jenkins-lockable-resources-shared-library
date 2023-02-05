#!groovy

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourceLabel;
import io.jenkins.library.lockableresources.ResourcesManager as RM;

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
@CheckForNull
Resource call(String resourceName) {
  return new Resource(RM.getResourceOrDie(resourceName));
}

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
@CheckForNull
Resource find(String resourceName) {
  return new Resource(RM.getResource(resourceName));
}

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
List<Resource> find(List<String> resourceNames) {
  return RM.getResources(resourceNames);
}

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
List<Resource> find(ResourceLabel resourceLabel, int quantity = 0) {
  return RM.getResources(resourceLabel, ['quantity' : quantity]);
}

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
List<Resource> find(ResourceLabel resourceLabel, Map opts) {
  return RM.getResources(resourceLabel, opts);
}

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
List<Resource> find(int quantity, Closure closure) {
  return RM.getResources(closure, ['quantity' : quantity]);
}

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
List<Resource> find(Map opts, Closure closure) {
  return RM.getResources(closure, opts);
}

//-----------------------------------------------------------------------------
/** 
*/
//@NonCPS
List<Resource> find(Closure closure) {
  return RM.getResources(closure, ['quantity' : 0]);
}

//-----------------------------------------------------------------------------
//@NonCPS
List<Resource> getAll() {
  return RM.getResources();
}

//------------------------------------------------------------------------------
//@NonCPS
boolean isFree(@NonNull String resourceName) {
  Resource resource = new Resource(resourceName);
  return resource.isFree();
}

