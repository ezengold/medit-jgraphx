package models;

public class BooleanVariable {
  private String name;
  private boolean value;

  public BooleanVariable(String name) {
    this.name = name;
    this.value = true;
  }

  public BooleanVariable(String name, boolean value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean getValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }
}
