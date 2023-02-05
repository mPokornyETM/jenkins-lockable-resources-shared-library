#!groovy

import jenkins.model.Jenkins;
import io.jenkins.library.lockableresources.ResourceLabel;
import io.jenkins.library.lockableresources.Utils;

//-----------------------------------------------------------------------------
void call(String nodeName, Closure closure) {
  lockNode(nodeName, [:], closure);
}

//-----------------------------------------------------------------------------
void call(final String nodeName, Map opts, Closure closure) {
  opts = Utils.fixNullMap(opts);

  if (Jenkins.get().getNode(nodeName) != null) {
    mirrorNodesToLockableResources(nodeName, opts.mirrorOptions);
    opts.remove('mirrorOptions');
    echo("Trying to acquire lock on node [$nodeName]");
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
        echo("Trying to acquire lock on node [$nodeName]");
        lockResource(matchedNode, opts) {
          inLockScope(matchedNode, opts, closure);
        }
      } else {
        echo("Trying to acquire lock on node [$nodeName]");
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
  echo("Lock acquired on node [$nodeName]");
  if (opts.allocateExecutor) {
    opts.remove('allocateExecutor');
    echo("Trying to acquire executor on node [$nodeName]");
    node(nodeName) {
      echo("Executor acquired on node [$nodeName]");
      inLockScope(nodeName, opts);
    }
    return;
  }

  closure();
}
