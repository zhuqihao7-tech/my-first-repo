package edu.uob;

public class Triangle extends TwoDimensionalShape implements MultiVariantShape {

  int a, b, c;

  private  TriangleVariant variant;
  private static int population = 0;

  public Triangle(int a, int b, int c) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.variant = determineVariant();
      population = population + 1;
  }
  private TriangleVariant determineVariant() {
      if(a <=0 || b<=0 || c<=0) {
          return TriangleVariant.ILLEGAL;
      }
      if((long)a + (long) b <= c || (long)b + (long)c <= a || (long)c + (long) a <= b) {
          if((long)a + (long)b == c || (long)a + (long)c == b || (long)b + (long)c == a) {
              return TriangleVariant.FLAT;
          }else{
              return TriangleVariant.IMPOSSIBLE;
          }
      }
      if(a == b && b == c) {
          return TriangleVariant.EQUILATERAL;
      }
      //int[] sides = {a, b, c};
      //java.util.Arrays.sort(sides);
      long side0 = (long)a;
      long side1 = (long)b;
      long side2 = (long)c;
      if(side0 * side0 + side1 * side1 == side2 * side2 || side0 * side0 + side2 * side2 == side1 * side1 || side1 *side1 + side2 * side2 == side0 * side0) {
          return TriangleVariant.RIGHT;
      }
      if(a == b || b == c || a == c){
          return TriangleVariant.ISOSCELES;
      }
      return TriangleVariant.SCALENE;
  }
  public int getLongestSide() {
      int max = a;
      if(b > max) {
        max = b;
      }
      if(c > max) {
        max = c;
      }
      return max;
  }
  public TriangleVariant getVariant(){
    return variant;
  }

  public String toString(){
      return getColourString() + " Triangle with sides of length " + a + "," + b + "," + c + " and variant: " + variant;
  }
  public double calculateArea() {
      double s = (a + b + c) / 2.0;
    return Math.sqrt(s * (s - a) * (s - b) * (s - c));
  }

  public int calculatePerimeterLength() {
    return a + b + c;
  }

  public static int getPopulation() {
    return population;
  }
}
