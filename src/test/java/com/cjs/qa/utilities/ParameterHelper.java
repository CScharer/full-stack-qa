package com.cjs.qa.utilities;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

public class ParameterHelper {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ParameterHelper.class));
  private static final boolean INCLUDE_LOCAL_VARIABLES = true;
  private List<Parameter> parameterList = null;
  private String className = null;
  private String methodName = null;
  private Integer lineNumber = null;
  private Boolean isStatic = null;
  private Integer countParameters = null;
  private Integer countVariables = null;

  public ParameterHelper(String className, String methodName, Integer lineNumber) {
    this.parameterList = new ArrayList<>();
    this.className = className;
    this.methodName = methodName;
    this.lineNumber = lineNumber;
  }

  /**
   * Gets the parameters for the calling method.
   *
   * @param parameterList
   * @return
   */
  public static String getParameters(List<List<Object>> parameterList) {
    return getParameters(parameterList, false);
  }

  /**
   * Gets the parameters for the calling method.
   *
   * @param parameterList
   * @param includeMethodVariables
   * @return
   */
  public static String getParameters(
      List<List<Object>> parameterList, boolean includeMethodVariables) {
    // LOG.debug("{}", JavaHelpers.createBufferString("*", 20, ""));
    ParameterHelper parameterHelper = null;
    try {
      String methodName = JavaHelpers.getCallingMethodName();
      final StackTraceElement[] stackTraceArray = new Throwable().getStackTrace();
      StackTraceElement stackTraceElement = stackTraceArray[2];
      methodName = stackTraceElement.getMethodName();
      String className = stackTraceElement.getClassName();
      parameterHelper =
          new ParameterHelper(className, methodName, JavaHelpers.getCallingLineNumber());
      Class<?> classRef = Class.forName(className);
      Method[] methodArray = classRef.getMethods();
      boolean methodFound = false;
      for (Method method : methodArray) {
        String methodArrayName = method.getName();
        if (methodArrayName.equals(methodName)) {
          Class<?> declaringClass = method.getDeclaringClass();
          ClassLoader declaringClassLoader = declaringClass.getClassLoader();
          Type typeDeclaringClass = Type.getType(declaringClass);
          String methodDescriptor = Type.getMethodDescriptor(method);
          String url = typeDeclaringClass.getInternalName() + ".class";
          try (InputStream inputStream = declaringClassLoader.getResourceAsStream(url)) {
            if (inputStream == null) {
              throw new IllegalArgumentException(
                  "The constructor's class loader cannot find the bytecode that"
                      + " defined the constructor's class (URL: "
                      + url
                      + ")");
            }
            // ASM 7.1 is now used exclusively (old asm:asm:3.3.1 excluded from QuickBooks
            // dependency)
            ClassNode classNode = new ClassNode();
            new ClassReader(inputStream).accept(classNode, 0);
            List<MethodNode> methodNodeList = classNode.methods;
            for (MethodNode methodNode : methodNodeList) {
              if (methodNode.name.equals(methodName) && methodNode.desc.equals(methodDescriptor)) {
                methodFound = true;
                parameterHelper.setIsStatic(method.toString().contains(" static "));
                parameterHelper =
                    getParameterData(parameterHelper, method, methodNode, parameterList);
                return parameterHelper.toString();
              }
            }
          }
        }
      }
      if (!methodFound) {
        LOG.debug(
            "Could not find method ["
                + parameterHelper.toString()
                + "].  Please ensure that the method is a public method.");
      }
    } catch (Exception e) {
      // e.printStackTrace();
      LOG.error("Error in {}: [{}]", JavaHelpers.getCurrentMethodName(), e.getMessage());
    }
    return parameterHelper != null ? parameterHelper.toString() : "null";
  }

  /**
   * Gets the parameter data based on static/non-static, parameters and/or variables.
   *
   * @param parameterHelper
   * @param method
   * @param methodNode
   * @param parameterList
   * @return
   */
  private static ParameterHelper getParameterData(
      ParameterHelper parameterHelper,
      Method method,
      MethodNode methodNode,
      List<List<Object>> parameterList) {
    try {
      if (parameterList.size() != method.getParameterCount()) {
        LOG.warn(
            "The argumentList passed in with a count of ({}) does not match the method.getParameterCount of ({}).",
            parameterList.size(),
            method.getParameterCount());
      }
      parameterHelper.setCountParameters(method.getParameterCount());
      List<LocalVariableNode> localVariableNodeList = methodNode.localVariables;
      parameterHelper.setCountVariables(localVariableNodeList.size());
      // Class<?>[] parameterTypeArray = method.getParameterTypes();
      // Type[] typeDescArray = Type.getArgumentTypes(methodNode.desc);
      Map<String, Object> parameterFieldMap = null;
      if (INCLUDE_LOCAL_VARIABLES) {
        // The first local variable represents the "this" object if the
        // method is not static!
        if (!parameterHelper.getIsStatic()) {
          parameterFieldMap = getParameterFields();
          LocalVariableNode localVariableNode = localVariableNodeList.get(0);
          parameterFieldMap.put("name", localVariableNode.name);
          parameterFieldMap.put("type", localVariableNode.desc);
          parameterFieldMap.put("value", localVariableNode.toString());
          Parameter parameter =
              new Parameter(
                  parameterHelper.getParameterList().size(),
                  parameterFieldMap.get("name").toString(),
                  parameterFieldMap.get("type").toString(),
                  parameterFieldMap.get("value"));
          parameterHelper.add(parameter);
        }
      }
      // Get Parameters
      for (int parameterIndex = 0; parameterIndex < parameterList.size(); parameterIndex++) {
        parameterFieldMap = getParameterFields();
        String parameterName = "arg" + parameterIndex;
        parameterName = parameterList.get(parameterIndex).get(0).toString();
        parameterFieldMap.put("name", parameterName);
        Object objectValue = parameterList.get(parameterIndex).get(1);
        parameterFieldMap.put("type", objectValue.getClass().toString());
        parameterFieldMap.put("value", objectValue);
        Parameter parameter =
            new Parameter(
                parameterHelper.getParameterList().size(),
                parameterFieldMap.get("name").toString(),
                parameterFieldMap.get("type").toString(),
                parameterFieldMap.get("value"));
        parameterHelper.add(parameter);
      }
      // Get Variables
      if (INCLUDE_LOCAL_VARIABLES) {
        int parameterCount = parameterHelper.getParameterList().size();
        // The first local variable represents the "this" object if the
        // method is not static!
        for (LocalVariableNode localVariableNode : localVariableNodeList) {
          parameterFieldMap = getParameterFields();
          if (!parameterHelper.containsName(localVariableNode.name)) {
            parameterFieldMap.put("name", localVariableNode.name);
            parameterFieldMap.put("type", localVariableNode.desc);
            parameterFieldMap.put("value", localVariableNode.toString());
            Parameter parameter =
                new Parameter(
                    parameterHelper.getParameterList().size() - parameterCount,
                    parameterFieldMap.get("name").toString(),
                    parameterFieldMap.get("type").toString(),
                    parameterFieldMap.get("value"));
            parameterHelper.add(parameter);
          }
        }
      }
    } catch (Exception e) {
      // e.printStackTrace();
      LOG.error("Error in {}: [{}]", JavaHelpers.getCurrentMethodName(), e.getMessage());
    }
    return parameterHelper;
  }

  public boolean containsName(String name) {
    for (Parameter parameter : getParameterList()) {
      if (parameter.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  private static Map<String, Object> getParameterFields() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", null);
    map.put("type", null);
    map.put("value", null);
    return map;
  }

  public void add(Parameter parameter) {
    getParameterList().add(parameter);
  }

  public String getClassName() {
    return className;
  }

  public Integer getCountParameters() {
    return countParameters;
  }

  public Integer getCountVariables() {
    return countVariables;
  }

  public Boolean getIsStatic() {
    return isStatic;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public String getMethodName() {
    return methodName;
  }

  public List<Parameter> getParameterList() {
    return parameterList;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setCountParameters(Integer countParameters) {
    this.countParameters = countParameters;
  }

  public void setCountVariables(Integer countVariables) {
    this.countVariables = countVariables;
  }

  public void setIsStatic(Boolean isStatic) {
    this.isStatic = isStatic;
  }

  public void setLineNumber(Integer lineNumber) {
    this.lineNumber = lineNumber;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public void setParameterList(List<Parameter> parameterList) {
    this.parameterList = parameterList;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Class Name:");
    stringBuilder.append(getClassName());
    stringBuilder.append("\n");
    stringBuilder.append("Method Name:");
    stringBuilder.append(getMethodName());
    stringBuilder.append(",");
    stringBuilder.append("Line Number:");
    stringBuilder.append(getLineNumber());
    stringBuilder.append(",");
    stringBuilder.append("Is Static:");
    stringBuilder.append(getIsStatic());
    stringBuilder.append(",");
    stringBuilder.append("Parameters:");
    stringBuilder.append(getCountParameters());
    stringBuilder.append(",");
    stringBuilder.append("Variables:");
    stringBuilder.append(getCountVariables());
    List<Parameter> parameterList = getParameterList();
    if (!parameterList.isEmpty()) {
      stringBuilder.append(Constants.NEWLINE + "\tParameters:");
    }
    for (Parameter parameter : parameterList) {
      stringBuilder.append(Constants.NEWLINE + "\t\tIndex:");
      stringBuilder.append(parameter.getIndex());
      stringBuilder.append(",Name:");
      stringBuilder.append(parameter.getName());
      stringBuilder.append(",Type:");
      stringBuilder.append(parameter.getType());
      stringBuilder.append(",Value:");
      Object value = parameter.getValue();
      if (value == null) {
        stringBuilder.append("null");
      } else {
        stringBuilder.append(value.toString());
      }
    }
    return stringBuilder.toString();
  }
}
