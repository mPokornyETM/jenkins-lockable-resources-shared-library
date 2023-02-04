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
// @NonCPS
void call(@NonNull String resourceName, @NonNull Map opts, @NonNull Closure closure) {
  Resource resource = new Resource(resourceName);

  if (opts.createOnDemand != null && !ResourcesManager.resourceExists(resourceName)) {
    final Map props = (opts.createOnDemand instanceof Map) ? opts.createOnDemand : [:];
    resource.create(props);
    opts.createOnDemand = null;
  }
  _lock(resource, opts, closure);
}

//------------------------------------------------------------------------------
// @NonCPS
void _lock(@NonNull String resourceName, @NonNull Map opts, @NonNull Closure closure) {
  _lock(new Resource(resourceName), opts, closure);
}


//------------------------------------------------------------------------------
// @NonCPS
void _lock(@NonNull Resource resource, @NonNull Map opts, @NonNull Closure closure) {

  try {
    if (opts.beforeLock != null) {
      opts.beforeLock(resource);
    }
    lock(resource.getName()) {
      _insideLock(resource, opts, closure);
    }
    if (opts.afterRelease != null) {
      opts.afterRelease(resource);
    }
  } catch (error) {
    if (opts.onFailure(resource, error) != true) {
      // not handled exception
      throw error;
    }
  }
}

//------------------------------------------------------------------------------
void _insideLock(@NonNull Resource resource, @NonNull Map opts, @NonNull Closure closure) {

  if (opts.timeout != null) {
    timeout(opts.timeout) {
      opts.timeout = null;
      _insideLock(resource, opts, closure);
    }
    return;
  }

  if (opts.onLock != null) {
    opts.onLock(resource);
  }

  closure();

  if (opts.beforeRelease != null) {
    opts.beforeRelease(resource);
  }
}