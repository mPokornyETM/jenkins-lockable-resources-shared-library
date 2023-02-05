#!groovy

import jenkins.model.Jenkins;
import io.jenkins.library.lockableresources.ResourceLabel;

//-----------------------------------------------------------------------------
void call(String nodeName, Closure closure) {
  lockNode(nodeName, [:], closure);
}

//-----------------------------------------------------------------------------
void call(final String nodeName, Map opts, Closure closure) {
  if (opts == null) {
    opts = [:]
  }

  if (Jenkins.get().getNode(nodeName) != null) {
    mirrorNodesToLockableResources(nodeName, opts.mirrorOptions);
    opts.remove('mirrorOptions');
    lockResource(nodeName, opts) {
      inLockScope(nodeName, opts, closure);
    }
  } else {
    // your node does not exists, we try to find it as label
    def matched = findNodesByLabel(nodeName, opts);
    if (matched.size()) {
      throw(new Exception('No matches for: ' + nodeName));
    }

    for(int i = 0; i < matched.size(); i++) {
      String matchedNode = matched[i];
      if (i == (matched.size() -1)) {
        lockResource(matchedNode, opts) {
          inLockScope(matchedNode, opts, closure);
        }
      } else {
        lockResource(matchedNode, opts) {}
      }
    }
  }
}

//-----------------------------------------------------------------------------
List<Resource> findNodesByLabel(String labelExpression, Map opts) {
  final Label parsed = Label.parseExpression(labelExpression);
  if (opts.quantity == null) {
    opts.quantity = 1; // per default lock only 1 node
  }
  return lockableResource.find(opts) {it -> return it.hasLabel(ResourceLabel.NODE_LABEL) && it.matches(parsed)};
}

//-----------------------------------------------------------------------------
void inLockScope(String nodeName, Map opts, Closure closure) {
  if (opts.allocateExecutor) {
    opts.remove('allocateExecutor');
    node(nodeName) {
      inLockScope(nodeName, opts);
    }
    return;
  }

  closure();
}
