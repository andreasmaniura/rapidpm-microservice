/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package junit.org.rapidpm.microservice.optionals.cli;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.dependencies.core.net.PortUtils;
import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.optionals.cli.helper.ExitHelper;

import java.util.Optional;

public class BaseCmdlineTest {

  @BeforeClass
  public static void setUpClass() {
    final PortUtils portUtils = new PortUtils();

    System.setProperty(Main.REST_HOST_PROPERTY, "127.0.0.1");
    System.setProperty(Main.SERVLET_HOST_PROPERTY, "127.0.0.1");

    System.setProperty(Main.REST_PORT_PROPERTY, portUtils.nextFreePortForTest() + "");
    System.setProperty(Main.SERVLET_PORT_PROPERTY, portUtils.nextFreePortForTest() + "");
  }


  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(getClass().getPackage().getName());
  }


  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }


  protected int startAndGetExit(String[] args) throws Throwable {
    JunitExitHelper.reset();
    Main.deploy(Optional.of(args));
    Main.stop();
    if (JunitExitHelper.exitCalled) {
      return JunitExitHelper.exitCode;
    }
    Assert.fail("Exit not called");
    return -1;
  }

  @ResponsibleFor(ExitHelper.class)
  public static class ExitHelperResolver implements ClassResolver<ExitHelper> {

    @Override
    public Class<? extends ExitHelper> resolve(Class<ExitHelper> interf) {
      return JunitExitHelper.class;
    }
  }

  public static class JunitExitHelper implements ExitHelper {

    public static int exitCode = -1;
    public static boolean exitCalled;

    public static void reset() {
      exitCode = -1;
      exitCalled = false;
    }

    @Override
    public void exit(int exitCode) {
      JunitExitHelper.exitCode = exitCode;
      exitCalled = true;
    }
  }
}
