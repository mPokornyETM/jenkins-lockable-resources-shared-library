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

  Map mirrorOptions = opts.mirrorOptions;
  Utils.fixNullMap(mirrorOptions);

  if (Jenkins.get().getNode(nodeName) != null) {
    echo "mirrorNodesToLockableResources $nodeName"
    mirrorNodesToLockableResources(nodeName, mirrorOptions);
    
    echo("Trying to acquire lock on node [$nodeName]");
    lockResource(nodeName, opts) {
      inLockScope(nodeName, opts, closure);
    }
  } else {
    // your node does not exists, we try to find it as label
    echo "findNodesByLabel $nodeName, $opts"
    List<Resource> matched = findNodesByLabel(nodeName, opts);
    if (matched.size() == 0) {
      throw(new Exception('No matches for: ' + nodeName));
    } else if (matched.size() == 1) {
      // exact one node, so call me back recursive, but with exact node name
      lockNode(matched[0].getName(), opts, closure);
    } else {
      // mirror all requested nodes
      for(Resource resource : matched) {
        mirrorNodesToLockableResources(resource.getName(), mirrorOptions);
      }
    }

    echo('Trying to acquire lock on ' + matched.size() + " node(s) [$nodeName]");
    lockResource(matched, opts, closure);
    echo('Trying to acquire lock on ' + matched.size() + " node(s) [$nodeName]");
    /*
    for(int i = 0; i < matched.size(); i++) {
      String matchedNode = matched[i].getName();
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
    */
  }
}

//-----------------------------------------------------------------------------
//@NonCPS
List<Resource> findNodesByLabel(String labelExpression, Map opts) {
  final Label parsed = Label.parseExpression(labelExpression);
  if (opts.quantity == null) {
    opts.quantity = 1; // per default lock only 1 node
  }

  if (opts.randomize == null) {
    opts.randomize = true; // make sense to randomize the node usage per default
  }
  if (opts.orderBy == null) {
    opts.orderBy = true;
  }
  echo "lockableResource.find"
  return lockableResource.find(opts) {it -> return it.hasLabel(ResourceLabel.NODE_LABEL) && it.matches(parsed)};
}

//-----------------------------------------------------------------------------
void inLockScope(String nodeName, Map opts, Closure closure) {
  
  if (opts.allocateExecutor) {
    opts.remove('allocateExecutor');
    echo("Trying to acquire executor on node [$nodeName]");
    node(nodeName) {
      echo("Executor acquired on node [$nodeName]");
      inLockScope(nodeName, opts, closure);
    }
    echo("Executor released on resource [$nodeName]");
    return;
  }

  echo("Lock acquired on node [$nodeName]");
  closure();
  echo("Lock released on node [$nodeName]");
}
