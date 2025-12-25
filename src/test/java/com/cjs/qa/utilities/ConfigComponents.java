package com.cjs.qa.utilities;

import java.util.Locale;

public enum ConfigComponents {
  FRAMEWORK("Framework", "/Framework/", "Framework Configuration"),
  CJS("Core", "/Core/", "Core Configuration");

  private String component;
  private String path;
  private String description;

  ConfigComponents(String component, String path, String description) {
    this.component = component;
    this.path = path;
    this.description = description;
  }

  public String getComponent() {
    return component;
  }

  public String getPath() {
    return path;
  }

  public String getDescription() {
    return description;
  }

  public String pathProject() {
    return Project.pathWorkspace() + path;
  }

  public static ConfigComponents fromString(String name) {
    return getEnumFromString(ConfigComponents.class, name);
  }

  /**
   * A common method for all enums since they can't have another base class
   *
   * @param <T> Enum type
   * @param c enum type. All enums must be all caps.
   * @param string case insensitive
   * @return corresponding enum, or null
   */
  public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
    return Enum.valueOf(c, string.trim().toUpperCase(Locale.ENGLISH));
  }
}
