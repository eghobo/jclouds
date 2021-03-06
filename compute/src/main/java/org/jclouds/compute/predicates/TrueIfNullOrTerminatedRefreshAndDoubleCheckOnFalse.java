/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.compute.predicates;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class TrueIfNullOrTerminatedRefreshAndDoubleCheckOnFalse implements Predicate<AtomicReference<NodeMetadata>> {

   private final GetNodeMetadataStrategy client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TrueIfNullOrTerminatedRefreshAndDoubleCheckOnFalse(GetNodeMetadataStrategy client) {
      this.client = client;
   }

   public boolean apply(AtomicReference<NodeMetadata> atomicNode) {
      NodeMetadata node = atomicNode.get();
      if (checkState(node))
         return true;
      node = refresh(node);
      atomicNode.set(node);
      return checkState(node);
   }

   public boolean checkState(NodeMetadata node) {
      if (node == null)
         return true;
      logger.trace("%s: looking for node state %s: currently: %s", node.getId(), NodeState.TERMINATED, node.getState());
      return node.getState() == NodeState.TERMINATED;
   }

   private NodeMetadata refresh(NodeMetadata node) {
      return client.getNode(node.getId());
   }
}
