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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents parameters for copying a media resource and optionally
 *                 deleting the source.
 *             
 * 
 * <p>Java class for CloneMediaParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CloneMediaParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ParamsType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
 *         &lt;element name="IsSourceDelete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
@XmlType(name = "CloneMediaParams", propOrder = {
    "source",
    "isSourceDelete"
})
public class CloneMediaParams
    extends ParamsType<CloneMediaParams>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromCloneMediaParams(this);
   }

   public static class Builder extends ParamsType.Builder<CloneMediaParams> {
      
      private Reference source;
      private Boolean isSourceDelete;

      /**
       * @see CloneMediaParams#getSource()
       */
      public Builder source(Reference source) {
         this.source = source;
         return this;
      }

      /**
       * @see CloneMediaParams#getIsSourceDelete()
       */
      public Builder isSourceDelete(Boolean isSourceDelete) {
         this.isSourceDelete = isSourceDelete;
         return this;
      }


      public CloneMediaParams build() {
         CloneMediaParams cloneMediaParams = new CloneMediaParams();
         cloneMediaParams.setSource(source);
         cloneMediaParams.setIsSourceDelete(isSourceDelete);
         return cloneMediaParams;
      }


      @Override
      public Builder fromParamsType(ParamsType<CloneMediaParams> in) {
          return Builder.class.cast(super.fromParamsType(in));
      }
      public Builder fromCloneMediaParams(CloneMediaParams in) {
         return fromParamsType(in)
            .source(in.getSource())
            .isSourceDelete(in.isSourceDelete());
      }
   }

   private CloneMediaParams() {
      // For JAXB and builder use
   }



    @XmlElement(name = "Source", required = true)
    protected Reference source;
    @XmlElement(name = "IsSourceDelete")
    protected Boolean isSourceDelete;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setSource(Reference value) {
        this.source = value;
    }

    /**
     * Gets the value of the isSourceDelete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSourceDelete() {
        return isSourceDelete;
    }

    /**
     * Sets the value of the isSourceDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSourceDelete(Boolean value) {
        this.isSourceDelete = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CloneMediaParams that = CloneMediaParams.class.cast(o);
      return equal(source, that.source) && 
           equal(isSourceDelete, that.isSourceDelete);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(source, 
           isSourceDelete);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("source", source)
            .add("isSourceDelete", isSourceDelete).toString();
   }

}
