/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.deltacloud;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jclouds.Constants;
import org.jclouds.deltacloud.domain.DeltacloudCollection;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.Image;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.InstanceAction;
import org.jclouds.deltacloud.domain.InstanceState;
import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.options.CreateInstanceOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * Tests behavior of {@code DeltacloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class DeltacloudClientLiveTest {

   protected DeltacloudClient client;
   protected RestContext<DeltacloudClient, DeltacloudAsyncClient> context;

   protected String provider = "deltacloud";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected Predicate<IPSocket> socketTester;

   protected void setupCredentials() {
      identity = System.getProperty("test." + provider + ".identity", "mockuser");
      credential = System.getProperty("test." + provider + ".credential", "mockpassword");
      endpoint = System.getProperty("test." + provider + ".endpoint", "http://localhost:3001/api");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
            overrides);

      client = context.getApi();
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 180, 1, TimeUnit.SECONDS);
   }

   @Test
   public void testGetLinksContainsAll() throws Exception {
      Set<? extends DeltacloudCollection> links = client.getCollections();
      assertNotNull(links);
   }

   @Test
   public void testGetInstanceStatesCanGoFromStartToFinish() throws Exception {
      Multimap<InstanceState, ? extends Transition> states = client.getInstanceStates();
      assertNotNull(states);
      Iterable<Transition> toFinishFromStart = findChainTo(InstanceState.FINISH, InstanceState.START, states);
      assert Iterables.size(toFinishFromStart) > 0 : toFinishFromStart;
      Iterable<Transition> toRunningFromStart = findChainTo(InstanceState.RUNNING, InstanceState.START, states);
      assert Iterables.size(toRunningFromStart) > 0 : toRunningFromStart;
      Iterable<Transition> toFinishFromRunning = findChainTo(InstanceState.FINISH, InstanceState.RUNNING, states);
      assert Iterables.size(toFinishFromRunning) > 0 : toFinishFromRunning;
      assertEquals(ImmutableList.copyOf(Iterables.concat(toRunningFromStart, toFinishFromRunning)),
            ImmutableList.copyOf(toFinishFromStart));
   }

   Iterable<Transition> findChainTo(InstanceState desired, InstanceState currentState,
         Multimap<InstanceState, ? extends Transition> states) {
      for (Transition transition : states.get(currentState)) {
         if (currentState.ordinal() >= transition.getTo().ordinal())
            continue;
         if (transition.getTo() == desired)
            return ImmutableSet.<Transition> of(transition);
         Iterable<Transition> transitions = findChainTo(desired, transition.getTo(), states);
         if (Iterables.size(transitions) > 0)
            return Iterables.concat(ImmutableSet.of(transition), transitions);
      }
      return ImmutableSet.<Transition> of();
   }

   public void testListAndGetRealms() throws Exception {
      Set<? extends Realm> response = client.listRealms();
      assert null != response;
      long realmCount = response.size();
      assertTrue(realmCount >= 0);
      for (Realm realm : response) {
         Realm newDetails = client.getRealm(realm.getHref());
         assertEquals(realm, newDetails);
      }
   }

   public void testListAndGetImages() throws Exception {
      Set<? extends Image> response = client.listImages();
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         Image newDetails = client.getImage(image.getHref());
         assertEquals(image, newDetails);
      }
   }

   public void testListAndGetHardwareProfiles() throws Exception {
      Set<? extends HardwareProfile> response = client.listHardwareProfiles();
      assert null != response;
      long profileCount = response.size();
      assertTrue(profileCount >= 0);
      for (HardwareProfile profile : response) {
         HardwareProfile newDetails = client.getHardwareProfile(profile.getHref());
         assertEquals(profile, newDetails);
      }
   }

   public void testListAndGetInstances() throws Exception {
      Set<? extends Instance> response = client.listInstances();
      assert null != response;
      long instanceCount = response.size();
      assertTrue(instanceCount >= 0);
      for (Instance instance : response) {
         Instance newDetails = client.getInstance(instance.getHref());
         assertEquals(instance, newDetails);
      }
   }

   protected String prefix = System.getProperty("user.name") + ".test";
   protected Instance instance;

   public void testCreateInstance() throws Exception {
      Logger.getAnonymousLogger().info("starting instance");
      instance = client.createInstance(Iterables.get(client.listImages(), 0).getId(),
            CreateInstanceOptions.Builder.named(prefix));
      instance = client.getInstance(instance.getHref());
      checkStartedInstance();

      Instance newInfo = client.getInstance(instance.getHref());
      checkInstanceMatchesGet(newInfo);

   }

   protected void checkInstanceMatchesGet(Instance newInfo) {
      assertEquals(newInfo.getHref(), instance.getHref());
   }

   protected void checkStartedInstance() {
      System.out.println(new Gson().toJson(instance));
      assertEquals(instance.getName(), prefix);
      assertEquals(instance.getState(), InstanceState.RUNNING);
   }

   @Test(dependsOnMethods = "testCreateInstance")
   public void testConnectivity() throws Exception {
      Logger.getAnonymousLogger().info("awaiting ssh");
      // TODO
      // assert socketTester.apply(new IPSocket(Iterables.get(instance.getPublicAddresses(), 0),
      // 22)) : instance;
      // doConnectViaSsh(instance, getSshCredentials(instance));
   }

   private Credentials getSshCredentials(Instance instance2) {
      // TODO
      return null;
   }

   public HttpRequest refreshInstanceAndGetAction(InstanceAction action) {
      return client.getInstance(instance.getHref()).getActions().get(action);
   }

   @Test(dependsOnMethods = "testConnectivity")
   public void testLifeCycle() throws Exception {
      client.performAction(refreshInstanceAndGetAction(InstanceAction.STOP));
      assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.STOPPED);

      client.performAction(refreshInstanceAndGetAction(InstanceAction.START));
      assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.RUNNING);

      client.performAction(refreshInstanceAndGetAction(InstanceAction.REBOOT));
      assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.RUNNING);

   }

   @Test(dependsOnMethods = "testLifeCycle")
   public void testDestroyInstance() throws Exception {
      try {
         client.performAction(refreshInstanceAndGetAction(InstanceAction.STOP));
         assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.STOPPED);
      } catch (IllegalArgumentException e) {
      }
      client.performAction(refreshInstanceAndGetAction(InstanceAction.DESTROY));
      assertEquals(client.getInstance(instance.getHref()), null);
   }

   protected void doConnectViaSsh(Instance instance, Credentials creds) throws IOException {
      SshClient ssh = Guice.createInjector(new JschSshClientModule()).getInstance(SshClient.Factory.class)
            .create(new IPSocket(Iterables.get(instance.getPublicAddresses(), 0), 22), creds);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         System.err.println(ssh.exec("df -k").getOutput());
         System.err.println(ssh.exec("mount").getOutput());
         System.err.println(ssh.exec("uname -a").getOutput());
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      try {
         // client.deleteResource(instance.getHref());
      } catch (Exception e) {
         // no need to check null or anything as we swallow all
      }
      if (context != null)
         context.close();
   }

}
