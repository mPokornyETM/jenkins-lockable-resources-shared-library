#!groovy


import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.SHORT;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jenkins.model.Jenkins;
import io.jenkins.library.lockableresources.Resource;
import io.jenkins.library.lockableresources.ResourceLabel;


//-----------------------------------------------------------------------------
void call(@NonNull String  nodeName) {
  mirrorNodeToLockableResource(nodeName, [:]);
}

//-----------------------------------------------------------------------------
void call(@NonNull String  nodeName, @NonNull Map opts) {
  mirrorNodeToLockableResource(nodeName, opts);
}

//-----------------------------------------------------------------------------
void call() {
  mirrorNodesToLockableResources([:]);
}

//-----------------------------------------------------------------------------
/** 
 Currently are nodes only added or updated, but not removed. There is good reason.
 We want to destroy your instance.
 Maybe when we add enabled option in the 'LockableResource', we can disable it.
 Also is the question what shall happens, when is currently locked and node does not
 exists?
*/
void call(@NonNull Map opts) {

  // synchronized over all jobs
  lock('mirrorNodes') {
    // mirror existing nodes
    List<String> mirrored = [];
    jenkins.model.Jenkins.instance.computers.each { c ->
      String resourceName = mirrorNodeToLockableResource(c, opts);
      if (resourceName != null) {
        mirrored.push(resourceName);
      }
    }

    // step all resources and check if the node has been removed 
    final ResourceLabel nodeLabel = new ResourceLabel(ResourceLabel.NODE_LABEL);
    for(Resource resource : lockableResource.find(nodeLabel)) {
      if (mirrored.contains(resource.getName())) {
        return;
      }
      resource.setNote('This resource is not a ' + nodeLabel.getName() + '\n' + resource.getNote());
      if (resource.isFree()) {
        resource.removeLabel(nodeLabel);
      }
      resource.save();
    }
  }
  
}

//-----------------------------------------------------------------------------
@NonCPS
@CheckForNull
Map nodeToResourceProperties(Computer computer) {
  if (computer == null || computer.node == null) {
    return null; // this node does not exists
  }

  final String nodeName = computer.node.selfLabel;

  final DateFormat format = SimpleDateFormat.getDateTimeInstance(MEDIUM, SHORT);
  final String url = Jenkins.get().getRootUrl() + computer.getUrl();
  String note = '';
  def formatter = Jenkins.get().getMarkupFormatter();
  if (formatter != null && formatter.class.name.toLowerCase().contains('markdown')) {
    // markdown formatter (like https://github.com/jenkinsci/markdown-formatter-plugin)
    note += '[' + nodeName + '](' + url + ')' + '\n';
    note += '\n';
    note += 'Last update at ' + format.format(new Date());
  } else if (formatter != null && formatter.class.name.toLowerCase().contains('html')) {
    // html formatter
    
    note += '<a';
    note += '  class="jenkins-table__link model-link"';
    note += '  href="' + url + '"';
    note += '  >' + nodeName + '<button';
    note += '    class="jenkins-menu-dropdown-chevron"';
    note += '  ></button';
    note += '></a>';
    note += '<br>';
    note += 'Last update at <strong>' + format.format(new Date()) + '</strong>';
  } else {
    // no formatter chosen (or not supported)
    note += url + '\n';
    note += 'Last update at ' + format.format(new Date());
  }
  
  return [
    'description' : computer.getDescription(),
    'labels' : ResourceLabel.NODE_LABEL + ' ' + computer.node.labelString,
    'note' : note,
    'name' : nodeName
  ];
}

//-----------------------------------------------------------------------------
@NonCPS
String  mirrorNodeToLockableResource(@NonNull String nodeName, @NonNull Map opts) {
  Computer computer = jenkins.model.Jenkins.instance.getComputer(nodeName);
  if (computer == null) {
    return; // this node does not exists
  }
  return mirrorNodeToLockableResource(computer, opts);
}

//-----------------------------------------------------------------------------
@NonCPS
String mirrorNodeToLockableResource(@NonNull Computer computer, @NonNull Map opts) {
  if (computer == null) {
    return null; // this node does not exists
  }

  Map properties = nodeToResourceProperties(computer);
  if (opts.nodeToResourceProperties != null) {
    opts.nodeToResourceProperties(computer, properties);
  }
echo "properties $computer.name " + properties;
  Resource resource = new Resource(properties.name ? properties.name : computer.name);
  if (!resource.exists()) {
    echo "crr properties $computer.name " + properties;
    resource.create(properties);
  } else {
    resource.fromMap(properties);
    resource.save();
  }

  return resource.getName();
}