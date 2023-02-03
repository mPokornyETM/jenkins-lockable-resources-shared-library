#!groovy


import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourcesManager;

//-----------------------------------------------------------------------------
void call(@NonNull String resourceName, @NonNull def closure) {
  lockResource(resourceName, [:]);
}

//-----------------------------------------------------------------------------
// createOnDemand: create resource when does not exists
void call(@NonNull String resourceName, @NonNull Map opts, @NonNull def closure) {

  if (opts.createOnDemand && !ResourcesManager.resourceExists()) {
    Resource resource = new Resource(resourceName);
    resource.create();
    opts = null;
  }
  lockableResource._lock(resourceName, opts);
}
