package junit.org.rapidpm.microservice.propertyservice.impl;


import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.propertyservice.impl.PropertyServiceImpl;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyServiceTest001 {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Inject
  PropertyServiceImpl service;

  private static final String PROPERTY_KEY = "example.part01.001";
  private static final String PROPERTY_VALUE = "test001";

  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
    DI.activatePackages("org.rapidpm");
    DI.activateDI(this);

    //createPropertiesFile();
    service.init(this.getClass().getResource("example.properties").getPath());
  }

  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }

  @Test
  public void test001() throws Exception {
    final String singleProperty = service.getSingleProperty(PROPERTY_KEY);

    Assert.assertNotNull(singleProperty);
    Assert.assertFalse(singleProperty.isEmpty());
    Assert.assertEquals(PROPERTY_VALUE, singleProperty);
  }

  @Test
  public void test002() throws Exception {
    final String singleProperty = service.getSingleProperty("example.invalid");
    Assert.assertTrue(singleProperty.isEmpty());
  }

  private void createPropertiesFile() throws IOException {
    Properties props = new Properties();
    final File file = temporaryFolder.newFile("example.properties");
    FileOutputStream fos = new FileOutputStream(file);
    props.setProperty(PROPERTY_KEY, PROPERTY_VALUE);
    props.store(fos, "JUnit Test Properties");
  }
}