package edu.uob;

public abstract class TwoDimensionalShape {
  protected  Colour colour;

  public TwoDimensionalShape() {}

  public Colour getColour() {
      return colour;
  }
  public void setColour(Colour colour) {
      this.colour = colour;
  }
  public String getColourString() {
      return "This is a " + colour;
  }
  public abstract double calculateArea();

  public abstract int calculatePerimeterLength();
}
