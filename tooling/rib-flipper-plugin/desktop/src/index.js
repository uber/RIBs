import {
  colors,
  Button,
  DetailSidebar,
  ErrorBlock,
  FlipperPlugin,
  Glyph,
  LoadingIndicator,
  ManagedDataInspector,
  Panel,
  PersistedState,
  SearchInput,
  SearchBox,
  SearchIcon,
  styled,
  Toolbar,
  Tooltip} from 'flipper';
import React, {Component} from 'react';
import * as d3 from "d3";
import {exec} from 'child_process';
import {getHostForNode, getHostForNodeRecursive} from './utils';
import TreeChart from './TreeChart';

var ICON_BUG = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADC0lEQVRYR72XO4hTQRSGv7isq4uIiGn0wkZI4QN05YCCIFhZiYUYCx8rPlBZGxstbBRtxNoHPlbBB4LBykZUrERtojaKrCmyGhYhoILgY3VdOWHuZXZ27r25gWQgxZ2cmfnPOf/5z0yOmSMHTDnTHZvTjaNRKpV6yuXyZDfnIgAi0lupVP7Yh3djrgnA53nC4YuBcaAJ1rG7ABxpFTiQUwAz8puywV7gGtDj2B0CzotIfyuRDJ2exgGPR82MeAB9Al4A203KeoGJIAj66/X6zyxpnAag1dAZuwlTLbrHP2AYuJzlcLXNTMJisdhXrVZ/ecpXpw6HIFp1ph0SqudJY4uIPGyFB2kk1IPcCO0EbqQA+AosdGxU2Lyak0TCaJEJ5yzgNzAIvI4BcRPYAxSAmkVgnzPNMk4iYROAlctnwFJgiZlbBBwA5gNPROSpCfsx4Jyz1nUmErwkEuqiHcBd463dH24DB4FmyRlAQ0YfwuBsAh6rzgH3HEBRAGNJmM/n5zUaje9ABVgH/PWEfTQIgsF6vf7D899LQPVBgLkiMukS00fC/cBzEalaxppvzbtvfABWhLLsMXgDrLHSuBxYD4zEKeFW4DqwwNnsjkmHe8YosDIBgM0hXfsN2CciD0IHfST0MVYJuNaXgiQAIjLbCfuUO+cjYdmQa8gK3TtAw5c5AtaCW8oFYJu9iY+EobarnbL3InAG2JCRhGq+UduzqQT9Vi2xqymxHWuda4PRn6tsIZY0En4xDlwBtINGI1M7BpSc99tJQVpTytKO3Yuq4kmqgkcisjmtKbXSjk8Ap2J0IK0MTwKnw7W+aKS1Yy3J90aIrgK7HSBJSjgHUCFaFifDae14l/F8QA+1bkE2hjgSXjLkVdsxE4WRrCTU98Eq4K0VOm23dhTiUmBzazXwSi+xvlRkacfher2O9ZkPHwCN2Ecratp622rHAyIyHsPiz0YbXADaF1Q13Zu0ghprh4RJLyUl1/FCoTBcq9WOAmeT2O47PI2EHX2mxSphtx+o7qW0Y89wqwSnnfEf3gFzRYyLvhoAAAAASUVORK5CYII=';

export default class RibTree extends FlipperPlugin {

  constructor(props) {
    super(props);

    this.state = {
       selectedNodeId: '',
       searchTerm: '',
       activeNodeId: '',
    };

    // Listener used to resize tree graph and make sure it fits within window.
    window.addEventListener("resize", this.onWindowResize.bind(this));
  }

  static defaultPersistedState = {
    sessionId: null,
    treeData: [],
    idToNodes: {},
  };

  static persistedStateReducer = (
    persistedState: PersistedState,
    method: string,
    data: Object,
  ) => {
    var newState;

    // when new session is detected, clear previous data..
    if (data.sessionId != persistedState.sessionId) {
        newState = {
           sessionId: data.sessionId,
           treeData: [],
           idToNodes: {},
         };
    } else {
        newState = {
           sessionId: persistedState.sessionId,
           treeData: persistedState.treeData.slice(0),
           idToNodes: {...persistedState.idToNodes},
        }
    }

    var parentNode = newState.idToNodes[data.parent.id];
    var childNode = newState.idToNodes[data.router.id];

    if (method === 'ATTACHED') {
        console.log("Ribtree: attach " + data.parent.name + "(" + data.parent.id + ") -> " + data.router.name + "(" + data.router.id + ")");
        if (!childNode && !parentNode) {
            parentNode = {
              parent: {},
              ...data.parent,
              children: []
            };
            newState.treeData.push(parentNode);
            newState.idToNodes[parentNode.id] = parentNode;
        }
        if (childNode) {
            if (!parentNode) {
              parentNode = {
                 parent: {},
                 ...data.parent,
                 children: [childNode]
               };
              if (childNode.parent && childNode.parent.id) {
                console.error("Ribtree: Child node should have no parent!");
                return;
              }
              childNode.parent = parentNode;

              newState.treeData = newState.treeData.filter(function(data) {
                return data.id != childNode.id;
              });

              newState.treeData.push(parentNode);
              newState.idToNodes[parentNode.id] = parentNode;
            } else {
               if (childNode.parent && childNode.parent.id) {
                  console.error("Ribtree: Child node should have no parent!");
                  return;
               } else {
                  parentNode.children.push(childNode);
                  childNode.parent = parentNode;
                  newState.treeData = newState.treeData.filter(function(node) {
                     return node.id != childNode.id;
                  });
               }
            }
        } else {
           childNode = {
             ...data.router,
             parent: parentNode,
             children: []
           };
           parentNode.children.push(childNode);
           newState.idToNodes[childNode.id] = childNode;
        }
    }
    if (method === 'DETACHED') {
       console.log("Ribtree: detach " + data.parent.name + "(" + data.parent.id + ") -> " + data.router.name + "(" + data.router.id + ")");
       if (!childNode) {
         console.log("Ribtree: Child node does not exists!");
         return;
       } else {
         if (parentNode) {
           parentNode.children = parentNode.children.filter(function(node) {
             return node.id != data.router.id;
           });
           if (!parentNode.children.length && !parentNode.parent.id) {
             newState.idToNodes[parentNode.id] = undefined;
           }
         }
         newState.idToNodes[data.router.id] = undefined;
         newState.treeData = newState.treeData.filter(function(node) {
           return node.id != childNode.id && node.children.length;
         });
       }
    }
    return newState;
  };

  componentDidMount() {
    this._currentActivity = null;
  }

  componentWillUnmount() {
  }

  onWindowResize() {
    this.forceUpdate();
  }

  async onNodeClick(node) {
    // clears screenshot and view data of previously selected node
    if (this.state.selectedNodeId && this.props.persistedState.idToNodes[this.state.selectedNodeId]) {
      this.props.persistedState.idToNodes[this.state.selectedNodeId].viewData = undefined;
    }

    this.setState({
      selectedNodeId: node.data.id
    });
  }

  // Note: the activity containing the UI elements need to be on top of the stack for permalink to work
  onEditLayout(node) {
    this.props.selectPlugin('Inspector', node.viewClassName);
  }

  onNodeMouseOver(node) {
    this.setState({
      activeNodeId: node.data.id
    });
    var request = {
      id: node.data.id
    };
    this.client.call("SHOW_HIGHLIGHT", request);
  }

  onNodeMouseOut(node) {
    this.setState({
      activeNodeId: undefined
    });
    var request = {
      id: node.data.id
    };
    this.client.call("HIDE_HIGHLIGHT", request);
  }

  onValueChanged(e) {
    var searchTerm = e.target.value;
    this.setState({searchTerm});
  }

  render() {
    if (!this.props.persistedState.treeData.length) {
      return <ErrorBlock error="Your application hasn't rendered a RIB tree yet!" />;
    }

    var that = this;

    // Use last element in root arrays, to support multiple rib-based activities simultaneously
    var treeData = this.props.persistedState.treeData;
    var hosts = [];
    var rootIndex = 0;
    var lastHost;
    treeData.forEach(function(node, i) {
       var host = getHostForNode(node);
       if (host) {
         if (host != lastHost) {
           rootIndex = i;
           hosts.push(host.substring(host.lastIndexOf('.') + 1));
         }
         lastHost = host;
       }
    });
    this._currentActivity = lastHost;

    var hostElements = [];
    hosts.forEach(function(name, i) {
       hostElements.push(<span>{name}</span>);
       if (i < hosts.length - 1) {
         hostElements.push(<Glyph name="arrow-right" color={colors.macOSTitleBarIcon} size={16} />);
       }
    });

    return <div onMeasure={this.onMeasure}>
      <Toolbar>
        <SearchBox style={{width: 200, height: 30, margin: 6}}>
          <SearchIcon
            name="magnifying-glass"
            color={colors.macOSTitleBarIcon}
            size={16}
          />
          <SearchInput
            placeholder={'Search RIB'}
            onChange={(e) => this.onValueChanged(e)}
            value={this.state.searchTerm}
          />
        </SearchBox>
        <div style={{marginLeft: 'auto', marginRight: 'auto'}}>
          <Tooltip title={'Stack of Rib-based android activities. Application can have several activities rendering different Rib tree simultaneously.'}>
            {hostElements}
          </Tooltip>
        </div>
        <Tooltip title={'Submit bug or request feature'}>
          <Button href={'https://github.com/facebook/flipper/issues'} style={{marginLeft: 'auto', marginRight: 10}} compact={true}>
            <img src={ICON_BUG} style={{width: 20, height: 20}} />
          </Button>
        </Tooltip>
      </Toolbar>
      <TreeChart
         data={[treeData[rootIndex]]}
         width={window.innerWidth - 350}
         height={window.innerHeight}
         margin={{top: 20, right: 20, bottom: 20, left: 20}}
         duration={500}
         onNodeClick={this.onNodeClick.bind(this)}
         onNodeMouseOver={this.onNodeMouseOver.bind(this)}
         onNodeMouseOut={this.onNodeMouseOut.bind(this)}
         searchTerm={this.state.searchTerm}
         activeNodeId={this.state.activeNodeId}
      />
     </div>;
  }
}
