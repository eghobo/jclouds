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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;


/**
 * 
 *                 Represents a list of references to available networks.
 *             
 * 
 * <p>Java class for AvailableNetworks complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AvailableNetworks">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Network" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailableNetworks", propOrder = {
    "networks"
})
public class AvailableNetworks {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAvailableNetworks(this);
   }

   public static class Builder {
      
      private List<Reference> networks = Lists.newArrayList();

      /**
       * @see AvailableNetworks#getNetworks()
       */
      public Builder networks(List<Reference> networks) {
         this.networks = Lists.newArrayList(checkNotNull(networks, "networks"));
         return this;
      }

      /**
       * @see AvailableNetworks#getNetworks()
       */
      public Builder network(Reference network) {
         networks.add(checkNotNull(network, "network"));
         return this;
      }

      public AvailableNetworks build() {
         AvailableNetworks availableNetworks = new AvailableNetworks(networks);
         return availableNetworks;
      }


      public Builder fromAvailableNetworks(AvailableNetworks in) {
         return networks(in.getNetworks());
      }
   }

   private AvailableNetworks() {
      // For JAXB and builder use
   }

   private AvailableNetworks(List<Reference> networks) {
      this.networks = networks;
   }


    @XmlElement(name = "Network")
    protected List<Reference> networks;

    /**
     * Gets the value of the network property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the network property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetwork().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     * 
     * 
     */
    public List<Reference> getNetworks() {
        if (networks == null) {
            networks = Lists.newArrayList();
        }
        return this.networks;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AvailableNetworks that = AvailableNetworks.class.cast(o);
      return equal(networks, that.networks);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networks);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("network", networks).toString();
   }

}
