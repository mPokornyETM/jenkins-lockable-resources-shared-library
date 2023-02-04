#!groovy

import jenkins.model.Jenkins.instance;

//-----------------------------------------------------------------------------
void call(String nodeName, Closure closure) {
  lockNode(nodeName, [:], closure);
}

//-----------------------------------------------------------------------------
void call(String nodeName, Map opts, Closure closure) {
  if (opts == null) {
    opts = [:]
  }
  
  mirrorNodeToLockableResource(opts.mirrorOptions);

  lockResource(nodeName, opts) {
    inLockScope(nodeName, opts, closure);
  }
}

//-----------------------------------------------------------------------------
void inLockScope(String nodeName, Map opts, Closure closure) {
  if (opts.allocateExecutor) {
    opts.allocateExecutor = null;
    node(nodeName) {
      inLockScope(nodeName, opts);
    }
    return;
  }

  closure();
}

