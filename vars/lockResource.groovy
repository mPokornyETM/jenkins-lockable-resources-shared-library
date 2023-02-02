#!groovy


import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.library.lockableresources.ResourcesManager;

//-----------------------------------------------------------------------------
void call(@NonNull String resourceName) {
  lockResource(resourceName, [:]);
}

//-----------------------------------------------------------------------------
void call(@NonNull String resourceName, @NonNull Map opts) {
  
  if (opts.createOnDemand && ResourcesManager.) {

  }
  lockResource(resourceName, [:]);
}
