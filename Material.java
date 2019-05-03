public class Material {

  private double rho;
  private double Z;
  private double A;

  //create a mateial with density rho, and atomic and proton numbers of A and Z respectively.
  public Material(double rho, double Z, double A) {
    this.rho = rho;
    this.Z = Z;
    this.A = A;
  }

  //getters
  public double getRho() {return this.rho;}

  public double getZ() {return this.Z;}

  public double getA() {return this.A;}
}

//Stamp of Approval for FinalModel
