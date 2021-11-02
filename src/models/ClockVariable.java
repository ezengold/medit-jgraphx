package models;

public class ClockVariable {
  private String name;
  private long value;

  public ClockVariable(String name) {
    this.name = name;
    this.value = 0;
  }

  public ClockVariable(String name, long value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }
}
