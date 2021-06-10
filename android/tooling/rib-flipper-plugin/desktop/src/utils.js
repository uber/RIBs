// Returns the host activity for the given node
export function getHostForNode(node) {
    var host = getHostForNodeRecursive(node, true);
    if (host) {
      return host;
    }
    return getHostForNodeRecursive(node, false);
}

export function getHostForNodeRecursive(node, fromParent) {
    if (!node) {
      return;
    }
    if (node.hostClassName) {
      return node.hostClassName;
    }
    if (fromParent) {
      return getHostForNodeRecursive(node.parent, fromParent);
    } else if (node.children) {
      for (var i = 0; i < node.children.length; i++) {
        var host = getHostForNodeRecursive(node.children[i], fromParent);
        if (host) {
           return host;
        }
      }
    }
}

