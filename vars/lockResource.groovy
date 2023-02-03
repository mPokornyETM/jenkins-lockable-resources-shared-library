#!groovy


import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourcesManager;

//-----------------------------------------------------------------------------
void call(@NonNull String resourceName, @NonNull Closure closure) {
  lockResource(resourceName, [:], closure);
}

//-----------------------------------------------------------------------------
// createOnDemand: create resource when does not exists
@NonCPS
void call(@NonNull String resourceName, @NonNull Map opts, @NonNull Closure closure) {
  Resource resource = new Resource(resourceName);

  if (opts.createOnDemand && !ResourcesManager.resourceExists(resourceName)) {
    resource.create();
    opts = null;
  }
  _lock(resource, opts, closure);
}

//------------------------------------------------------------------------------
@NonCPS
void _lock(@NonNull Resource resource, @NonNull Map opts, @NonNull Closure closure) {
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
void _lock(@NonNull String resourceName, @NonNull Map opts, @NonNull Closure closure) {
  _lock(new Resource(resourceName), opts, closure);
}
