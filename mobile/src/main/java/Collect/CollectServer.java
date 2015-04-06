package Collect;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * Created by Vincent on 05/04/15.
 */
public class CollectServer {



    // -------------------------------------------------------------------
    // Collect Server
    // -------------------------------------------------------------------


    private Node[] nodeCache;
    private Node[] selectedNodes;
    private Hashtable<String,Node> nodeTable = new Hashtable<String,Node>();
    private DefaultListModel nodeModel;

    public void CollectServer(){
        nodeModel = new DefaultListModel();
        nodeModel.addElement("<All>");
    }
    // -------------------------------------------------------------------
    // Node Handling
    // -------------------------------------------------------------------

    public synchronized Node[] getNodes() {
        if (nodeCache == null) {
            Node[] tmp = nodeTable.values().toArray(new Node[nodeTable.size()]);
            Arrays.sort(tmp);
            nodeCache = tmp;
        }
        return nodeCache;
    }

    public Node addNode(String nodeID) {
        return getNode(nodeID, true);
    }

    private Node getNode(final String nodeID, boolean notify) {
        Node node = nodeTable.get(nodeID);
        if (node == null) {
            node = new Node(nodeID);
            nodeTable.put(nodeID, node);

            synchronized (this) {
                nodeCache = null;
            }

            /*
            if (notify) {
                final Node newNode = node;
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        boolean added = false;
                        for (int i = 1, n = nodeModel.size(); i < n; i++) {
                            int cmp = newNode.compareTo((Node) nodeModel.get(i));
                            if (cmp < 0) {
                                nodeModel.add(i, newNode);
                                added = true;
                                break;
                            } else if (cmp == 0) {
                                // node already added
                                added = true;
                                break;
                            }
                        }
                        if (!added) {
                            nodeModel.addElement(newNode);
                        }

                        if (visualizers != null) {
                            for (int i = 0, n = visualizers.length; i < n; i++) {
                                visualizers[i].nodeAdded(newNode);
                            }
                        }

                    }
                });
            }
            */
        }
        return node;
    }

    /*
    public void selectNodes(Node[] nodes) {
        selectNodes(nodes, true);
    }
    */

    /*
        private void selectNodes(Node[] nodes, boolean updateList) {
            if (nodes != selectedNodes) {
                selectedNodes = nodes;
                if (updateList) {
                    nodeList.clearSelection();
                    if (selectedNodes != null) {
                        for (int i = 0, n = selectedNodes.length; i < n; i++) {
                            int index = nodeModel.indexOf(selectedNodes[i]);
                            if (index >= 0) {
                                nodeList.addSelectionInterval(index, index);
                            }
                        }
                    }
                }
                if (visualizers != null) {
                    for (int i = 0, n = visualizers.length; i < n; i++) {
                        visualizers[i].nodesSelected(nodes);
                    }
                }
            }
        }
    */
    public Node[] getSelectedNodes() {
        return selectedNodes;
    }


    // -------------------------------------------------------------------
    // Serial communication
    // -------------------------------------------------------------------

}
