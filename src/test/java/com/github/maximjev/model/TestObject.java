package com.github.maximjev.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestObject {
  public enum TestEnum {VALUE_A, VALUE_B}

  private TestSubobject sub;
  private String ignoredDirect;
  private String str1;
  private Date date;
  private int int1;
  private List<String> list;
  private Map<String, String> mapOfStrings;
  private Map<Integer, String> mapWithIntKeys;
  private String[] stringArray;
  private byte[] byteArray;
  private int[] intArray;
  private TestObject[] objArray;
  private List<TestSubobject> listOfObjects;
  private Map<String, TestSubobject> mapOfObjects;
  private TestEnum testEnum;
  private URL url;
  private URI uri;
  private Class cls;
  private BigDecimal bigDecimal;
  private ZonedDateTime zonedDateTime;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
  private ZonedDateTime formattedZonedDateTime;
  @JsonProperty
  private String jsonPropNoValue;
  private UUID uuid;
  private TestObject recursion;
  private JsonNode jsonNode;
  private String widgetName;

  public String getStr1() {
    return str1;
  }

  public void setStr1(String str1) {
    this.str1 = str1;
  }

  public String getIgnoredDirect() {
    return ignoredDirect;
  }

  public void setIgnoredDirect(String ignoredDirect) {
    this.ignoredDirect = ignoredDirect;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public int getInt1() {
    return int1;
  }

  public void setInt1(int int1) {
    this.int1 = int1;
  }

  public TestSubobject getSub() {
    return sub;
  }

  public void setSub(TestSubobject sub) {
    this.sub = sub;
  }

  public List<String> getList() {
    return list;
  }

  public void setList(List<String> list) {
    this.list = list;
  }

  public Map<String, String> getMapOfStrings() {
    return mapOfStrings;
  }

  public void setMapOfStrings(Map<String, String> mapOfStrings) {
    this.mapOfStrings = mapOfStrings;
  }

  public Map<Integer, String> getMapWithIntKeys() {
    return mapWithIntKeys;
  }

  public void setMapWithIntKeys(Map<Integer, String> mapWithIntKeys) {
    this.mapWithIntKeys = mapWithIntKeys;
  }

  public String[] getStringArray() {
    return stringArray;
  }

  public void setStringArray(String[] stringArray) {
    this.stringArray = stringArray;
  }

  public byte[] getByteArray() {
    return byteArray;
  }

  public void setByteArray(byte[] byteArray) {
    this.byteArray = byteArray;
  }

  public int[] getIntArray() {
    return intArray;
  }

  public void setIntArray(int[] intArray) {
    this.intArray = intArray;
  }

  public TestObject[] getObjArray() {
    return objArray;
  }

  public void setObjArray(TestObject[] objArray) {
    this.objArray = objArray;
  }

  public List<TestSubobject> getListOfObjects() {
    return listOfObjects;
  }

  public void setListOfObjects(List<TestSubobject> listOfObjects) {
    this.listOfObjects = listOfObjects;
  }

  public Map<String, TestSubobject> getMapOfObjects() {
    return mapOfObjects;
  }

  public void setMapOfObjects(Map<String, TestSubobject> mapOfObjects) {
    this.mapOfObjects = mapOfObjects;
  }

  public TestEnum getTestEnum() {
    return testEnum;
  }

  public void setTestEnum(TestEnum testEnum) {
    this.testEnum = testEnum;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public Class getCls() {
    return cls;
  }

  public void setCls(Class cls) {
    this.cls = cls;
  }

  public BigDecimal getBigDecimal() {
    return bigDecimal;
  }

  public void setBigDecimal(BigDecimal bigDecimal) {
    this.bigDecimal = bigDecimal;
  }

  public ZonedDateTime getZonedDateTime() {
    return zonedDateTime;
  }

  public void setZonedDateTime(ZonedDateTime zonedDateTime) {
    this.zonedDateTime = zonedDateTime;
  }

  public ZonedDateTime getFormattedZonedDateTime() {
    return formattedZonedDateTime;
  }

  public void setFormattedZonedDateTime(ZonedDateTime formattedZonedDateTime) {
    this.formattedZonedDateTime = formattedZonedDateTime;
  }

  public String getJsonPropNoValue() {
    return jsonPropNoValue;
  }

  public void setJsonPropNoValue(String jsonPropNoValue) {
    this.jsonPropNoValue = jsonPropNoValue;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public TestObject getRecursion() {
    return recursion;
  }

  public void setRecursion(TestObject recursion) {
    this.recursion = recursion;
  }

  public JsonNode getJsonNode() {
    return jsonNode;
  }

  public void setJsonNode(JsonNode jsonNode) {
    this.jsonNode = jsonNode;
  }

  public String getStaticValue() {
    return "TEST";
  }

  public String getWidgetName() {
    return widgetName;
  }

  public void setWidgetName(String widgetName) {
    this.widgetName = widgetName;
  }
}
