package com.cjs.qa.maven.objects;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.XML;

public class Dependency {

  private String groupId;
  private String artifactId;
  private String version;
  private String type;
  private String scope;

  public Dependency(String dependency) {
    final String[] dependencyArray = dependency.split(":");
    this.groupId = dependencyArray[0];
    this.artifactId = dependencyArray[1];
    this.version = dependencyArray[3];
    this.type = dependencyArray[2];
    this.scope = dependencyArray[4];
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getScope() {
    return scope;
  }

  public String getType() {
    return type;
  }

  public String getVersion() {
    return version;
  }

  public String getVersionProperty() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<");
    stringBuilder.append(getArtifactId());
    stringBuilder.append(".version");
    stringBuilder.append(">");
    stringBuilder.append(getVersion());
    stringBuilder.append("</");
    stringBuilder.append(getArtifactId());
    stringBuilder.append(".version");
    stringBuilder.append(">");
    return stringBuilder.toString();
  }

  public String getVersionTag() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("${");
    stringBuilder.append(getArtifactId());
    stringBuilder.append(".version");
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(0, 1));
    stringBuilder.append(XML.ENCLOSURE_LEFT + "dependency" + XML.ENCLOSURE_RIGHT);
    stringBuilder.append(Constants.nlTab(1, 2));
    stringBuilder = XML.getWrappedField(stringBuilder, "groupId", getGroupId());
    stringBuilder.append(Constants.nlTab(1, 2));
    stringBuilder = XML.getWrappedField(stringBuilder, "artifactId", getArtifactId());
    stringBuilder.append(Constants.nlTab(1, 2));
    stringBuilder = XML.getWrappedField(stringBuilder, "version", getVersion());
    stringBuilder.append(Constants.nlTab(1, 2));
    stringBuilder = XML.getWrappedField(stringBuilder, "type", getType());
    stringBuilder.append(Constants.nlTab(1, 2));
    stringBuilder = XML.getWrappedField(stringBuilder, "scope", getScope());
    stringBuilder.append(Constants.nlTab(1, 1));
    stringBuilder.append(
        XML.ENCLOSURE_LEFT + XML.ENCLOSURE_DELIMETER + "dependency" + XML.ENCLOSURE_RIGHT);
    stringBuilder.append(Constants.nlTab(1, 0));
    return stringBuilder.toString();
  }
}
