package com.cjs.qa.junit.dbunit;

import java.io.FileReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.h2.Driver;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class BaseDBUnitTestForJPADao {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BaseDBUnitTestForJPADao.class));

  // Test class, single-threaded test execution context
  // Using synchronized initialization for thread-safe singleton pattern
  private static EntityManagerFactory entityManagerFactory = null;
  private static IDatabaseConnection iDatabaseConnection = null;
  private static final Object INIT_LOCK = new Object();

  private EntityManager entityManager = null;

  protected static EntityManagerFactory getEntityManagerFactory() {
    synchronized (INIT_LOCK) {
      return entityManagerFactory;
    }
  }

  protected static IDatabaseConnection getiDatabaseConnection() {
    synchronized (INIT_LOCK) {
      return iDatabaseConnection;
    }
  }

  protected EntityManager getEntityManager() {
    return entityManager;
  }

  protected void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Creates a database connection and transfers ownership to DatabaseConnection. The connection
   * will be closed by DatabaseConnection.close() in @AfterClass.
   *
   * @param url database URL
   * @param properties connection properties
   * @return Connection that will be managed by DatabaseConnection
   * @throws Exception if connection fails
   */
  private static Connection createDatabaseConnection(String url, Properties properties)
      throws Exception {
    return Driver.load().connect(url, properties);
  }

  @BeforeAll
  static void setupTestClass() throws Exception {
    synchronized (INIT_LOCK) {
      if (entityManagerFactory != null) {
        // Already initialized
        return;
      }
      LOG.debug("Setup-Class Method:[{}]", JavaHelpers.getCurrentClassName());
      final Properties properties = new Properties();
      properties.put("user", DBInfo.USER);
      properties.put("password", DBInfo.PASSWORD);

      // Connection is wrapped by DatabaseConnection and closed in @AfterClass via
      // iDatabaseConnection.close()
      // DatabaseConnection takes ownership of the Connection and will close it
      // Use try-with-resources to ensure connection is closed if DatabaseConnection creation fails
      try (Connection connection = createDatabaseConnection(DBInfo.URL, properties)) {
        // Transfer ownership to DatabaseConnection - it will close the connection
        // when iDatabaseConnection.close() is called in @AfterClass
        iDatabaseConnection = new DatabaseConnection(connection);
        // Connection is now owned by DatabaseConnection, so we don't close it here
        // The try-with-resources will only close it if an exception occurs before
        // DatabaseConnection takes ownership
        String dbLocation = Constants.PATH_PROJECT + "b2csite.dll" + IExtension.SQL;
        dbLocation = "src/test/resources/tabledef/b2csite.dll" + IExtension.SQL;
        // FileReader is closed automatically by try-with-resources
        try (FileReader fileReader = new FileReader(dbLocation)) {
          RunScript.execute(iDatabaseConnection.getConnection(), fileReader);
        }
        // Connection successfully transferred to DatabaseConnection
        // It will be closed by iDatabaseConnection.close() in @AfterClass
      } catch (final Exception e) {
        // If DatabaseConnection was created, close it (which will close the underlying connection)
        if (iDatabaseConnection != null) {
          try {
            iDatabaseConnection.close();
          } catch (final Exception closeException) {
            // Connection will be closed by DatabaseConnection.close()
            LOG.warn("Error closing database connection: {}", closeException.getMessage());
          }
        }
        // If try-with-resources didn't close the connection (shouldn't happen),
        // it will be closed automatically when the try block exits
        throw e;
      }

      final Map<Object, Object> mapProperties = new HashMap<>();
      mapProperties.put("javax.persistence.jdbc.url", DBInfo.URL);
      // mapProperties.put("hibernate.hbm2dll.auto", "create-drop");
      entityManagerFactory =
          Persistence.createEntityManagerFactory("orderPersistenceUnit", mapProperties);
    }
  }

  @AfterAll
  static void teardownTestClass() {
    synchronized (INIT_LOCK) {
      LOG.debug("TearDown-Class Method:[{}]", JavaHelpers.getCurrentClassName());
      try {
        if (iDatabaseConnection != null) {
          iDatabaseConnection.close();
          iDatabaseConnection = null;
        }
        if (entityManagerFactory != null) {
          if (entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
          }
          entityManagerFactory = null; // Test teardown in single-threaded context
        }
      } catch (final Exception e) {
        // Intentionally empty - cleanup failure is non-critical
        if (Environment.isLogAll()) {
          LOG.warn("Cleanup failure (non-critical): {}", e.getMessage());
        }
      }
    }
  }

  @BeforeEach
  void baseSetup() throws Exception {
    setEntityManager(entityManagerFactory.createEntityManager());
  }

  @AfterEach
  void baseTeardown() throws Exception {
    if (getEntityManager() != null) {
      if (getEntityManager().isOpen()) {
        getEntityManager().close();
      }
      setEntityManager(null);
    }
  }
}
