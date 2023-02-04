#!groovy


import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.SHORT;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jenkins.model.Jenkins;
import io.jenkins.library.lockableresources.Resource;


//-----------------------------------------------------------------------------
void call(@NonNull String  nodeName) {
  mirrorNodeToLockableResource(nodeName, [:]);
}

//-----------------------------------------------------------------------------
void call(@NonNull String  nodeName, @NonNull String Map opts) {
  mirrorNodeToLockableResource(nodeName, opts);
}

void call() {
  mirrorNodesToLockableResources([:]);
}

void call(@NonNull String Map opts) {
  jenkins.model.Jenkins.instance.computers.each { c ->
    mirrorNodeToLockableResource(c, opts);
  }
}

//-----------------------------------------------------------------------------
@NonCPS
@CheckForNull
Map nodeToResourceProperties(Computer computer) {
  if (computer == null) {
    return null; // this node does not exists
  }

  final DateFormat format = SimpleDateFormat.getDateTimeInstance(MEDIUM, SHORT);
  final String url = Jenkins.get().getRootUrl() + computer.getUrl();
  String note = '';
  def formatter = Jenkins.get().getMarkupFormatter();
  if (formatter != null && formatter.class.name.toLowerCase().contains('markdown')) {
    // markdown formatter (like https://github.com/jenkinsci/markdown-formatter-plugin)
    note += '[' + computer.getName() + '](' + url + ')' + '\n';
    note += '\n';
    note += 'Last update at ' + format.format(new Date());
  } else if (formatter != null && formatter.class.name.toLowerCase().contains('html')) {
    // html formatter
    
    note += '<a';
    note += '  class="jenkins-table__link model-link"';
    note += '  href="' + url + '"';
    note += '  >' + computer.getName() + '<button';
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
    'labels' : computer.node.labelString + ' node',
    'note' : note
  ];
}

//-----------------------------------------------------------------------------
@NonCPS
void mirrorNodeToLockableResource(@NonNull String  nodeName, @NonNull String Map opts) {
  Computer computer = jenkins.model.Jenkins.instance.getComputer(nodeName);
  if (computer == null) {
    return; // this node does not exists
  }
  mirrorNodeToLockableResource(computer, opts);
}

//-----------------------------------------------------------------------------
@NonCPS
void mirrorNodeToLockableResource(@NonNull String Computer computer, @NonNull String Map opts) {
  if (computer == null) {
    return; // this node does not exists
  }

  Map properties = nodeToResourceProperties(computer);
  if (opts.nodeToResourceProperties != null) {
    opts.nodeToResourceProperties(computer, properties);
  }

  Resource resource = new Resource(properties.name ? properties.name : computer.name);
  if (!resource.exists()) {
    resource.create(properties);
  } else {
    resource.fromMap(properties);
    resource.save();
  }
}