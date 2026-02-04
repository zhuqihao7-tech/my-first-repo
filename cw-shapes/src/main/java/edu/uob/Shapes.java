package edu.uob;

public class Shapes {

  // TODO use this class as then entry point; play around with your shapes, etc
  public static void main(String[] args) {
    /*System.out.println("Hello world!");
      Triangle testTriangle = new Triangle(5, 5, 5);
      System.out.println(testTriangle.calculateArea());
      System.out.println(testTriangle.calculatePerimeterLength());
      if(testTriangle instanceof MultiVariantShape) System.out.println("This shape has multiple variants");
      else System.out.println("This shape has only one variant");
      testTriangle.setColour(Colour.BLUE);
      int longestSide = testTriangle.getLongestSide();
      System.out.println("The longest side of the triangle is " + longestSide);
      System.out.println(testTriangle.toString());
      TwoDimensionalShape shape;
      shape = new Circle(5);
      System.out.println(shape.toString());
      if(shape instanceof MultiVariantShape) System.out.println("This shape has multiple variants");
      else System.out.println("This shape has only one variant");
      shape = new Triangle(4, 5, 7);
      System.out.println(shape.toString());
      shape = new Rectangle(4,6);
      System.out.println(shape.toString());*/

      TwoDimensionalShape[] shapes = new TwoDimensionalShape[100];
      for(int i = 0; i < shapes.length; i++){
          double r = Math.random();
          if(r < 0.33) shapes[i] = new Circle(1);
          else if (r < 0.66) {
              shapes[i] = new Rectangle(1, 1);
          }
          else shapes[i] = new Triangle(1, 1, 1);
      }
      int count = 0;
      for(int i = 0; i < shapes.length; i++){
          System.out.println(shapes[i].toString());
          if(shapes[i] instanceof Triangle) count++;
      }
      System.out.println(count + " triangles have been created");
      System.out.println("Triangle count using class variable: " + Triangle.getPopulation());
      for (TwoDimensionalShape s : shapes) {

          if (s instanceof Triangle) {
              Triangle t = (Triangle) s;
              System.out.println(t.getVariant());
          }
      }

  }
}
